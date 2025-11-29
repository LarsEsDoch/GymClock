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
    private val proximitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    private val gravitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    private var isNear = false
    private var isUpsideDown = false

    private var lastState: Boolean? = null
    private var confirmationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        proximitySensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        gravitySensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        confirmationJob?.cancel()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_PROXIMITY -> {
                // isNear is true if the object is very close
                isNear = event.values[0] < (proximitySensor?.maximumRange ?: 5f)
            }
            Sensor.TYPE_GRAVITY -> {
                // Check if the phone is upside down (Y-axis of gravity is strongly negative)
                val y = event.values[1]
                isUpsideDown = y < -9.5
            }
        }
        checkPocketState()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun checkPocketState() {
        val currentState = isNear && isUpsideDown

        if (currentState != lastState) {
            confirmationJob?.cancel() // Cancel any pending confirmation
            confirmationJob = scope.launch {
                delay(CONFIRMATION_DELAY_MS) // Wait for a short period
                if (currentState == (isNear && isUpsideDown)) { // Re-check the condition after delay
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
