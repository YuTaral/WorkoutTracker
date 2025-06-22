package com.example.workouttracker.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.ui.managers.VibrationEvent
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.Constants.TIMER_END_VIBRATION
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** View model to control the state of Timer Dialog */
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val context: Application,
    private val vibrationManager: VibrationManager
): ViewModel() {

    /** The time left */
    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft = _timeLeft.asStateFlow()

    /** Track whether the timer is running or is paused */
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    /** Track whether the time finished */
    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    /** Track whether the app is in the background */
    private val _isAppInBackground = MutableStateFlow(false)
    val isAppInBackground = _isAppInBackground.asStateFlow()

    /** The initial amount of time in seconds */
    var initialSeconds: Long = 0L
        private set

    /** Job to refresh the timer on each seconds */
    private var timerJob: Job? = null

    /** Callback used to send notification if necessary */
    private lateinit var sendNotificationCallback: (Context) -> Unit

    /**
     * Initializes the timer with the given number of seconds and starts it if autoStart is true.
     * @param seconds The initial number of seconds to count down from.
     * @param autoStart Whether the timer should start immediately.
     */
    fun initializeData(seconds: Long, autoStart: Boolean, sendNotification: (Context) -> Unit) {
        initialSeconds = seconds
        _timeLeft.value = seconds
        _isRunning.value = autoStart
        sendNotificationCallback = sendNotification
        _isFinished.value = false

        if (autoStart) {
            runTimer()
        }
    }

    /** Update the value whether the app is in the background */
    fun updateIsInBackground(value: Boolean) {
        _isAppInBackground.value = value
    }

    /** Update the running state of the timer or resets it if the timer has finished */
    fun updateRunning() {
        if (_isFinished.value) {
            reset()
            return
        }

        _isRunning.value = !_isRunning.value

        if (_isRunning.value)  {
            runTimer()
        } else {
            timerJob?.cancel()
        }
    }

    /**
     * Starts the countdown coroutine and updates timeLeft every second.
     */
    private fun runTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isRunning.value && _timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value -= 1
            }

            if (_timeLeft.value <= 0) {
                _isFinished.value = true
                _isRunning.value = false
                onTimerFinish()
            }
        }
    }

    /** Resets the timer to its initial state and restarts it. */
    fun reset() {
        timerJob?.cancel()
        _timeLeft.value = initialSeconds
        _isRunning.value = true
        _isFinished.value = false

        runTimer()
    }

    /** Called when the timer finishes; triggers end-of-timer behavior (e.g., notification or vibration) */
    fun onTimerFinish() {
        if (_isAppInBackground.value) {
            sendNotificationCallback(context)
        } else {
            viewModelScope.launch {
                vibrationManager.makeVibration(
                    event = VibrationEvent(pattern = TIMER_END_VIBRATION)
                )
            }
        }
    }

    /** Cancel the job */
    fun cancelJob() {
        timerJob?.cancel()
    }
}