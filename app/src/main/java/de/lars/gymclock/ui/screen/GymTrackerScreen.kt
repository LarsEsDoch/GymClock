package de.lars.gymclock.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.lars.gymclock.ui.theme.GymClockTheme

@Composable
fun GymTrackerScreen(
    viewModel: GymTrackerViewModel = viewModel(),
    isInPocket: Boolean,
    onSettingsClicked: () -> Unit,
    onHistoryClicked: () -> Unit
) {
    val timerDisplay by viewModel.timerDisplay.observeAsState("00:00")
    val timerMode by viewModel.timerMode.observeAsState(GymTrackerViewModel.TimerMode.IDLE)

    LaunchedEffect(isInPocket) {
        if (isInPocket) {
            viewModel.startSetTimer()
        } else {
            if (timerMode == GymTrackerViewModel.TimerMode.SET_IN_PROGRESS) {
                viewModel.startRestTimer()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (title, subtitle) = when (timerMode) {
            GymTrackerViewModel.TimerMode.SET_IN_PROGRESS -> "Set in Progress" to "Your set is being timed."
            GymTrackerViewModel.TimerMode.RESTING -> "Resting" to "Your rest period is counting down."
            else -> "Ready" to "Put phone in pocket to start set, or start rest manually."
        }

        Text(text = title, style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle, 
            style = TextStyle(fontSize = 16.sp), 
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(text = timerDisplay, style = TextStyle(fontSize = 72.sp, fontWeight = FontWeight.Bold))

        Spacer(modifier = Modifier.height(48.dp))

        Row {
            if (timerMode == GymTrackerViewModel.TimerMode.IDLE) {
                Button(onClick = { viewModel.startRestTimer() }) {
                    Text("Start Rest")
                }
            }
            if (timerMode != GymTrackerViewModel.TimerMode.IDLE) {
                Button(onClick = { viewModel.stopTimer() }) {
                    Text("Stop")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(onClick = onSettingsClicked) {
                Text(text = "Settings")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onHistoryClicked) {
                Text(text = "History")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GymTrackerScreenPreview() {
    GymClockTheme {
        GymTrackerScreen(isInPocket = false, onSettingsClicked = {}, onHistoryClicked = {})
    }
}
