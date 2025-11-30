package de.lars.gymclock.ui.screen

import android.app.Application
import android.media.MediaPlayer
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import de.lars.gymclock.data.SettingsRepository
import de.lars.gymclock.data.WorkoutRepository
import de.lars.gymclock.db.WorkoutSet
import de.lars.gymclock.notification.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class GymTrackerViewModel(
    application: Application,
    private val workoutRepository: WorkoutRepository,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    enum class TimerMode {
        IDLE,
        SET_IN_PROGRESS,
        RESTING
    }

    private val _timerMode = MutableLiveData(TimerMode.IDLE)
    val timerMode: LiveData<TimerMode> = _timerMode

    private val _currentTimeMillis = MutableLiveData(0L)
    val timerDisplay: LiveData<String> = _currentTimeMillis.map { formatTime(it) }

    val allSets: LiveData<List<WorkoutSet>> = workoutRepository.allSets.asLiveData()

    private var timerJob: Job? = null
    var restTimeMillis: Long
        get() = settingsRepository.restTimeMillis
        set(value) {
            settingsRepository.restTimeMillis = value
        }

    private val soundPlayer: MediaPlayer by lazy {
        MediaPlayer.create(getApplication(), Settings.System.DEFAULT_NOTIFICATION_URI)
    }

    private val notificationHelper by lazy { NotificationHelper(getApplication()) }

    fun startSetTimer() {
        if (_timerMode.value == TimerMode.SET_IN_PROGRESS) return
        _timerMode.value = TimerMode.SET_IN_PROGRESS
        timerJob?.cancel() // Cancel any previous job
        playSound()

        val startTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            _currentTimeMillis.postValue(0L)
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                _currentTimeMillis.postValue(elapsed)
                delay(50)
            }
        }
    }

    fun startRestTimer() {
        if (_timerMode.value == TimerMode.RESTING) return
        _timerMode.value = TimerMode.RESTING
        timerJob?.cancel()
        playSound()

        viewModelScope.launch {
            workoutRepository.insertSet()
        }

        timerJob = viewModelScope.launch {
            var remainingTime = restTimeMillis
            _currentTimeMillis.postValue(remainingTime)

            while (remainingTime > 0) {
                delay(50)
                remainingTime -= 50
                _currentTimeMillis.postValue(if (remainingTime < 0) 0 else remainingTime)
            }

            _timerMode.postValue(TimerMode.IDLE)
            playSound()
            notificationHelper.showRestFinishedNotification()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerMode.value = TimerMode.IDLE
        _currentTimeMillis.value = 0L
    }

    private fun playSound() {
        soundPlayer.start()
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        soundPlayer.release()
        timerJob?.cancel()
    }
}