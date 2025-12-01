package de.lars.gymclock.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PocketDetector(context: Context, private val onPocketStateChanged: (Boolean) -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gravitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private var isUpsideDown = false
    private var isDark = false

    private var lastState: Boolean? = null
    private var confirmationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        gravitySensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        lightSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        confirmationJob?.cancel()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_GRAVITY -> {
                val y = event.values[1]
                isUpsideDown = y < -9.0
            }
            Sensor.TYPE_LIGHT -> {
                isDark = event.values[0] < 10.0f
            }
        }
        checkPocketState()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun checkPocketState() {
        val currentState = isUpsideDown && isDark

        if (currentState != lastState) {
            confirmationJob?.cancel()
            confirmationJob = scope.launch {
                delay(CONFIRMATION_DELAY_MS)
                if (currentState == (isUpsideDown && isDark)) {
                    lastState = currentState
                    withContext(Dispatchers.Main) {
                        onPocketStateChanged(currentState)
                    }
                }
            }
        }
    }

    companion object {
        private const val CONFIRMATION_DELAY_MS = 400L
    }
}
