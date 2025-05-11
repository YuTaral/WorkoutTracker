package com.example.workouttracker.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.network.repositories.ExerciseRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.components.dialogs.AddEditWorkoutDialog
import com.example.workouttracker.ui.components.dialogs.TimerDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


/** WorkoutsViewModel to manage the state of WorkoutScreen */
@HiltViewModel
class SelectedWorkoutViewModel @Inject constructor(
    var workoutRepository: WorkoutRepository,
    var userRepository: UserRepository,
    private var exerciseRepository: ExerciseRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** Seconds elapsed since the start of the workout */
    private val _secondsElapsed = MutableStateFlow(getWorkoutElapsedSeconds())
    val secondsElapsed = _secondsElapsed.asStateFlow()

    /** Timer to update the UI on each second */
    private var timerJob: Job? = null

    /**
     * Trigger the timer, calculating the seconds elapsed and optionally starting the timer job
     * @param start true to start the timer, false to set only the seconds elapsed value
     */
    fun triggerTimer(start: Boolean) {
        _secondsElapsed.value = getWorkoutElapsedSeconds()

        if (start) {
            restartTimer()
        } else {
            stopTimer()
        }
    }

    /** Cancel the timer */
    fun stopTimer() {
        if (timerJob != null) {
            timerJob!!.cancel()
        }
    }

    /** Display the add workout dialog */
    fun showAddWorkoutDialog() {
        Utils.showAddWorkoutDialog(viewModelScope, resourceProvider)
    }

    /** Display the edit workout dialog */
    @SuppressLint("StateFlowValueCalledInComposition")
    fun showEditWorkoutDialog() {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.edit_workout_title),
                dialogName = "AddEditWorkoutDialog",
                content = { AddEditWorkoutDialog(workout = workoutRepository.selectedWorkout.value!!) }
            )
        }
    }

    /**
     * Start the timer
     * @param seconds the seconds to start the timer with
     * @param setId the set id for which rest timer was called
     */
    fun startTimer(seconds: Long, setId: Long) {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.timer_lbl),
                dialogName = "TimerDialog",
                content = { TimerDialog(
                    seconds = seconds,
                    autoStart = true,
                    onDone = {
                        viewModelScope.launch(Dispatchers.IO) {
                            exerciseRepository.completeSet(
                                id = setId,
                                workoutId = workoutRepository.selectedWorkout.value!!.id,
                                onSuccess = {
                                    workoutRepository.updateSelectedWorkout(it)

                                    viewModelScope.launch(Dispatchers.IO) {
                                        workoutRepository.updateWorkouts(null)
                                    }

                                    viewModelScope.launch {
                                        DialogManager.hideDialog("TimerDialog")
                                    }
                                }
                            )
                        }
                    })
                }
            )
        }
    }

    /** Get the selected workout elapsed seconds */
    private fun getWorkoutElapsedSeconds(): Int {
        if (workoutRepository.selectedWorkout.value == null) {
            return 0
        }

        val workout = workoutRepository.selectedWorkout.value!!

        return if (workout.durationSeconds != null && workout.durationSeconds!! > 0) {
            workout.durationSeconds!!
        } else if (workout.startDateTime != null) {
            val now = Date()
            var elapsedMillis = now.time - workout.startDateTime.time

            if (elapsedMillis < 0) {
                // Make sure the value is not negative, some times there is offset
                // between the device and the server with ~20seconds
                elapsedMillis = 0
            }

            (elapsedMillis / 1000).toInt()
        } else {
            0
        }
    }

    /** Restart the timer and generate new value on each second */
    private fun restartTimer() {
        if (timerJob != null) {
            timerJob!!.cancel()
        }

        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _secondsElapsed.value += 1
            }
        }
    }
}