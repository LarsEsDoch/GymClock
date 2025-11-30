package de.lars.gymclock

import android.app.Application
import de.lars.gymclock.data.SettingsRepository
import de.lars.gymclock.data.WorkoutRepository
import de.lars.gymclock.db.AppDatabase
import de.lars.gymclock.notification.NotificationHelper

class GymClockApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { WorkoutRepository(database.workoutSetDao()) }
    val settingsRepository by lazy { SettingsRepository(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper(this).createNotificationChannel()
    }
}