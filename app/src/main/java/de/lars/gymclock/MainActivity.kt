package de.lars.gymclock

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.lars.gymclock.sensor.PocketDetector
import de.lars.gymclock.ui.screen.GymTrackerScreen
import de.lars.gymclock.ui.screen.GymTrackerViewModel
import de.lars.gymclock.ui.screen.GymTrackerViewModelFactory
import de.lars.gymclock.ui.screen.SettingsScreen
import de.lars.gymclock.ui.theme.GymClockTheme

class MainActivity : ComponentActivity() {

    private lateinit var pocketDetector: PocketDetector
    private var isInPocket by mutableStateOf(false)
    private val viewModel: GymTrackerViewModel by viewModels {
        val application = (application as GymClockApplication)
        GymTrackerViewModelFactory(application, application.repository, application.settingsRepository)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        // We can handle the result here if needed, but for now we do nothing.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pocketDetector = PocketDetector(this) { pocketState ->
            isInPocket = pocketState
        }

        setContent {
            GymClockTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "tracker") {
                    composable("tracker") {
                        GymTrackerScreen(
                            viewModel = viewModel,
                            isInPocket = isInPocket,
                            onSettingsClicked = { navController.navigate("settings") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            initialRestTimeSeconds = viewModel.restTimeMillis / 1000,
                            onSave = {
                                viewModel.restTimeMillis = it * 1000
                                navController.popBackStack()
                            },
                            onCancel = { navController.popBackStack() }
                        )
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
