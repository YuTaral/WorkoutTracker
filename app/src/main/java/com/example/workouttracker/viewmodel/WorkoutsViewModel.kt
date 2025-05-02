package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.managers.DatePickerDialogManager
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/** WorkoutsViewModel to manage the state of WorkoutScreen */
@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    var userRepository: UserRepository,
    var workoutRepository: WorkoutRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** Selected workouts start date */
    private var _startDate = MutableStateFlow(getDefaultStartDate())
    var startDate = _startDate.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            workoutRepository.updateWeightUnits()
            workoutRepository.updateWorkouts(_startDate.value)
            workoutRepository.updateSelectedWorkout(null)
        }
    }

    /**
     * Update the workouts start date
     * @param newDate the new start date
     */
    private fun updateStartDate(newDate: Date) {
        _startDate.value = newDate

        viewModelScope.launch(Dispatchers.IO) {
            workoutRepository.updateWorkouts(_startDate.value)
        }
    }

    /** Display the date picker dialog */
    fun showDatePicker() {
        viewModelScope.launch {
            DatePickerDialogManager.showDialog(
                onCancel = {
                    viewModelScope.launch {
                        DatePickerDialogManager.hideDialog()
                    }
                },
                onDatePick = { newDate ->
                    viewModelScope.launch {
                        DatePickerDialogManager.hideDialog()
                    }

                    updateStartDate(newDate)
                }
            )
        }
    }

    /** Display the add workout dialog */
    fun showAddWorkoutDialog() {
        Utils.showAddWorkoutDialog(viewModelScope, resourceProvider)
    }

    /**
     * Mark the workout as selected
     * @param workout selected workout, may be null (when deleted)
     */
    fun selectWorkout(workout: WorkoutModel?) {
        viewModelScope.launch {
            workoutRepository.updateSelectedWorkout(workout)
            PagerManager.changePageSelection(Page.SelectedWorkout)
        }
    }

    /** Return the default workouts start date - 1 month backwards */
    private fun getDefaultStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        return calendar.time
    }
}