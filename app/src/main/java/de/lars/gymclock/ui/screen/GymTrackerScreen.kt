package de.lars.gymclock.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.lars.gymclock.ui.theme.GymClockTheme

@Composable
fun GymTrackerScreen(
    viewModel: GymTrackerViewModel = viewModel(),
    isInPocket: Boolean
) {
    val timerDisplay by viewModel.timerDisplay.observeAsState("00:00")
    val timerMode by viewModel.timerMode.observeAsState(GymTrackerViewModel.TimerMode.IDLE)

    LaunchedEffect(isInPocket) {
        if (isInPocket) {
            viewModel.startSetTimer()
        } else {
            // Only start the rest timer if a set was in progress
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
            else -> "Ready" to "Put your phone in your pocket to start your set."
        }

        Text(text = title, style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = subtitle, style = TextStyle(fontSize = 16.sp))

        Spacer(modifier = Modifier.height(48.dp))

        Text(text = timerDisplay, style = TextStyle(fontSize = 72.sp, fontWeight = FontWeight.Bold))
    }
}

@Preview(showBackground = true)
@Composable
fun GymTrackerScreenPreview() {
    GymClockTheme {
        GymTrackerScreen(isInPocket = false)
    }
}
