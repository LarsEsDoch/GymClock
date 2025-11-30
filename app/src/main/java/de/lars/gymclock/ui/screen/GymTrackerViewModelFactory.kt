package de.lars.gymclock.ui.screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.lars.gymclock.data.SettingsRepository
import de.lars.gymclock.data.WorkoutRepository

class GymTrackerViewModelFactory(
    private val application: Application,
    private val workoutRepository: WorkoutRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GymTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GymTrackerViewModel(application, workoutRepository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}