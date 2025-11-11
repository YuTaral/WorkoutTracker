package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.TrainingPlanModel
import com.example.workouttracker.data.network.repositories.TrainingPlanRepository
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.managers.DatePickerDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.PagerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/** View model to control the state of assigned training plan dialog */
@HiltViewModel
class AssignedTRPlanViewModel @Inject constructor(
    var trainingPlanRepository: TrainingPlanRepository,
    private var workoutsRepository: WorkoutRepository,
    private var pagerManager: PagerManager,
    private var datePickerDialog: DatePickerDialogManager,
    private var dialogManager: DialogManager
): ViewModel() {

    /** The selected training program if any */
    private var _selectedTrainingPlan = MutableStateFlow(TrainingPlanModel())
    var selectedTrainingPlan = _selectedTrainingPlan.asStateFlow()

    /** Selected training plan start date */
    private var _startDate = MutableStateFlow(getDefaultStartDate())
    var startDate = _startDate.asStateFlow()

    /**
     * Initialize the data in the training plan view model
     * @param model the training plan model to select
     */
    fun initializeData(model: TrainingPlanModel) {
        _selectedTrainingPlan.value = model
        updateStartDate(getDefaultStartDate())

        if (_startDate.value < model.scheduledStartDate!!) {
            // Change the start on date if the training plan is scheduled for future date
            updateStartDate(model.scheduledStartDate!!)
        }
    }

    /** Display the date picker dialog */
    fun showDatePicker() {
        viewModelScope.launch {
            datePickerDialog.showDialog(
                onCancel = {
                    viewModelScope.launch {
                        datePickerDialog.hideDialog()
                    }
                },
                onDatePick = { newDate ->
                    viewModelScope.launch {
                        datePickerDialog.hideDialog()
                    }
                    updateStartDate(newDate)
                },
                allowPastDatesValue = false
            )
        }
    }

    /** Start the training plan */
    fun start() {
        // Modify the start date
        _selectedTrainingPlan.value.scheduledStartDate = _startDate.value

        viewModelScope.launch(Dispatchers.IO) {
            trainingPlanRepository.startTrainingPlan(
                trainingPlan = _selectedTrainingPlan.value,
                onSuccess = {
                    viewModelScope.launch {
                        workoutsRepository.updateWorkouts(null, null)
                        pagerManager.changePageSelection(Page.Workouts)
                        dialogManager.hideDialog("AssignedTRPlanDialog")
                    }
                }
            )
        }
    }

    /**
     * Update the training plan start date
     * @param newDate the new start date
     */
    private fun updateStartDate(newDate: Date) {
        _startDate.value = newDate
    }

    /** Return the default workouts start date - today */
    private fun getDefaultStartDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }
}