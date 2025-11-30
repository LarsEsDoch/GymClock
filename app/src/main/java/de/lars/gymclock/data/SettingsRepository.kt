package de.lars.gymclock.data

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var restTimeMillis: Long
        get() = prefs.getLong(KEY_REST_TIME, 60000L)
        set(value) = prefs.edit().putLong(KEY_REST_TIME, value).apply()

    companion object {
        private const val PREFS_NAME = "gym_clock_prefs"
        private const val KEY_REST_TIME = "rest_time"
    }
}