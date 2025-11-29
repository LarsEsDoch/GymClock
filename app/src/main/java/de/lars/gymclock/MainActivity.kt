package de.lars.gymclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lars.gymclock.sensor.PocketDetector
import de.lars.gymclock.ui.screen.GymTrackerScreen
import de.lars.gymclock.ui.theme.GymClockTheme

class MainActivity : ComponentActivity() {

    private lateinit var pocketDetector: PocketDetector
    private var isInPocket by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pocketDetector = PocketDetector(this) { pocketState ->
            isInPocket = pocketState
        }

        setContent {
            GymClockTheme {
                GymTrackerScreen(isInPocket = isInPocket)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pocketDetector.start()
    }

    override fun onPause() {
        super.onPause()
        pocketDetector.stop()
    }
}
