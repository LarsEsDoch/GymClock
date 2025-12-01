package de.lars.gymclock.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.lars.gymclock.ui.theme.GymClockTheme
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    initialRestTimeSeconds: Long,
    onSave: (Long) -> Unit,
    onCancel: () -> Unit
) {
    var restTime by remember { mutableStateOf(initialRestTimeSeconds.toFloat()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set Rest Time: ${restTime.roundToInt()} seconds")
        Spacer(modifier = Modifier.height(16.dp))
        Slider(
            value = restTime,
            onValueChange = { 
                restTime = it
            },
            onValueChangeFinished = {
                val roundedValue = (restTime.roundToInt() / 10) * 10
                restTime = roundedValue.toFloat()
            },
            valueRange = 10f..300f,
            steps = 28
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            Button(onClick = { onSave(restTime.toLong()) }) {
                Text("Save")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    GymClockTheme {
        SettingsScreen(initialRestTimeSeconds = 60, onSave = {}, onCancel = {})
    }
}
