package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

/** View model to control the UI state of Add / Edit workout dialog */
@HiltViewModel
class AddEditWorkoutViewModel @Inject constructor(
    private var workoutsRepository: WorkoutRepository,
    private var workoutTemplatesRepository: WorkoutTemplatesRepository,
    private var userRepository: UserRepository,
    private var resourceProvider: ResourceProvider,
    private val vibrationManager: VibrationManager,
    private var askQuestionManager: AskQuestionDialogManager,
    private val dialogManager: DialogManager,
    private val pagerManager: PagerManager
): ViewModel() {

    /** Class containing all fields in the UI */
    data class UIState(
        val name: String = "",
        val notes: String = "",
        val weightUnit: String = "",
        val nameError: String? = null,
        val scheduledForDate: Date? = null
    )

    /** The dialog modes */
    enum class Mode {
        ADD,
        EDIT
    }

    /** Dialog state */
    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    /** The dialog mode */
    private var mode = Mode.ADD

    /** The selected workout / template if any */
    private var selectedWorkout: WorkoutModel? = null

    /**
     * Update the UI sate to empty upon dialog recreation, as the view model is
     * HiltViewModel and is created only once per activity lifetime
     * @param workout the selected workout / template if any, null otherwise
     * @param dialogMode the dialog mode
     * @param scheduledForDate the scheduled for date if any (used when workout is assigned)
     */
    fun initialize(workout: WorkoutModel?, dialogMode: Mode, scheduledForDate: Date?) {
        mode = dialogMode
        selectedWorkout = workout

        if (workout == null) {
            updateName("")
            updateNotes("")
            updateWeightUnit(userRepository.user.value!!.defaultValues.weightUnit)
            updateScheduledForDate(null)
        } else {
            updateName(workout.name)
            updateNotes(workout.notes)
            updateWeightUnit(workout.weightUnit)
            updateWeightUnit(workout.weightUnit)
            updateScheduledForDate(scheduledForDate)
        }
    }

    /** Update the name in the UI with the provided value */
    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    /** Update the notes in the UI with the provided value */
    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    /** Update the name error in the UI with the provided value */
    fun updateNameError(value: String?) {
        _uiState.update { it.copy(nameError = value) }
    }

    /** Update the selected weight unit in the UI with the provided value */
    fun updateWeightUnit(value: String) {
        _uiState.update { it.copy(weightUnit = value) }
    }

    /** Update the scheduled for date with the provided value */
    fun updateScheduledForDate(date: Date?) {
        _uiState.update { it.copy(scheduledForDate = date) }
    }

    /**
     * Add/edit the workout if it's valid
     * @param assignedWorkoutId larger than 0 if the workout is not started from assignment
     */
    fun saveWorkout(assignedWorkoutId: Long) {
        if (!validate()) {
            return
        }

        if (mode == Mode.ADD) {
            startWorkout(assignedWorkoutId = assignedWorkoutId)
        } else {
            val workout = selectedWorkout!!
            workout.name = _uiState.value.name
            workout.notes = _uiState.value.notes
            workout.weightUnit = _uiState.value.weightUnit

            if (workout.template) {
                updateWorkoutTemplate(template = workout)
            } else {
                updateWorkout(workout = workout)
            }
        }
    }

    /** Delete the workout */
    fun deleteWorkout() {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.DELETE_WORKOUT,
                onConfirm = {
                    viewModelScope.launch(Dispatchers.IO) {
                        workoutsRepository.deleteWorkout(
                            workoutId = workoutsRepository.selectedWorkout.value!!.id,
                            onSuccess = {
                                viewModelScope.launch {
                                    onWorkoutActionSuccess(null, Page.Workouts)
                                }
                            }
                        )
                    }
                },
                formatQValues = listOf(workoutsRepository.selectedWorkout.value!!.name)
            ))
        }
    }

    /**
     * Start new workout
     * @param assignedWorkoutId the assigned workout id (if any)
     */
    private fun startWorkout(assignedWorkoutId: Long) {
        val newWorkout: WorkoutModel = if (selectedWorkout == null) {
            // Create new workout
            WorkoutModel(0, _uiState.value.name, false, mutableListOf(), _uiState.value.notes, null, 0, _uiState.value.weightUnit)
        } else {
            // Create new workout from the template
            WorkoutModel(0, _uiState.value.name, true, selectedWorkout!!.exercises, _uiState.value.notes, null, 0, _uiState.value.weightUnit)
        }

        if (_uiState.value.scheduledForDate != null && Utils.getDateDifferenceInDays(_uiState.value.scheduledForDate!!, Date()) != 0L) {
            showScheduledDateNotToday(newWorkout = newWorkout, assignedWorkoutId = assignedWorkoutId)
        } else {
            startWorkout(newWorkout = newWorkout, assignedWorkoutId = assignedWorkoutId)
        }
    }

    /**
     * Start new workout
     * @param newWorkout the workout data
     * @param assignedWorkoutId the assigned workout id (if any)
     */
    private fun startWorkout(newWorkout: WorkoutModel, assignedWorkoutId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutsRepository.addWorkout(
                workout = newWorkout,
                assignedWorkoutId = assignedWorkoutId,
                onSuccess = { createdWorkout ->
                    onWorkoutActionSuccess(createdWorkout, Page.SelectedWorkout)
                }
            )
        }
    }

    /**
     * Send request to update the workout
     * @param workout the workout to update
     */
    private fun updateWorkout(workout: WorkoutModel) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutsRepository.updateWorkout (
                workout = workout,
                onSuccess = { updatedWorkout ->
                    onWorkoutActionSuccess(updatedWorkout, Page.SelectedWorkout)
                }
            )
        }
    }

    /**
     * Send request to update the workout template
     * @param template the workout template to update
     */
    private fun updateWorkoutTemplate(template: WorkoutModel) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutTemplatesRepository.updateWorkoutTemplate(
                template = template,
                onSuccess = {
                    viewModelScope.launch {
                        dialogManager.hideDialog("AddEditWorkoutDialog")
                    }
                }
            )
        }
    }

    /**
     * Show message that scheduled date is not today
     * @param newWorkout the workout data
     * @param assignedWorkoutId the assigned workout id (if any)
     */
    private fun showScheduledDateNotToday(newWorkout: WorkoutModel, assignedWorkoutId: Long) {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.SCHEDULED_WORKOUT_DATE_MISS_MATCH,
                onConfirm = {
                    startWorkout(
                        newWorkout = newWorkout,
                        assignedWorkoutId = assignedWorkoutId
                    )
                },
                formatQValues = listOf(Utils.defaultFormatDate(_uiState.value.scheduledForDate!!))
            ))
        }
    }

    /**
     * Execute the logic when workout CRUD operation has been executed successfully
     * @param workout the workout to set as selected, may be null
     * @param redirectToPage the page to redirect to
     */
    private fun onWorkoutActionSuccess(workout: WorkoutModel?, redirectToPage: Page) {
        workoutsRepository.updateSelectedWorkout(workout)

        viewModelScope.launch(Dispatchers.IO) {
            workoutsRepository.updateWorkouts(null)

            withContext(Dispatchers.Main) {
                dialogManager.hideDialog("AddEditWorkoutDialog")
                pagerManager.changePageSelection(redirectToPage)
            }
        }
    }

    /** Validate the fields in the UI, return true if valid, false otherwise */
    private fun validate(): Boolean {
        if (_uiState.value.name.isEmpty()) {
            viewModelScope.launch { vibrationManager.makeVibration() }
            updateNameError(resourceProvider.getString(R.string.error_msg_enter_workout_name))
            return false
        } else {
            updateNameError(null)
        }

        return true
    }
}