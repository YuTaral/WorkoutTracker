package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AddEditWorkoutUiState(
    val name: String = "",
    val notes: String = "",
    val nameError: String? = null
)

/** View model to control the UI state of Add / Edit workout dialog */
@HiltViewModel
class AddEditWorkoutViewModel @Inject constructor(
    private var workoutsRepository: WorkoutRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** Dialog state */
    private val _uiState = MutableStateFlow(AddEditWorkoutUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Update the UI sate to empty upon dialog recreation, as the view model is
     * HiltViewModel and is created only once per activity lifetime
     * @param workout the selected workout if any, null otherwise
     */
    fun initialize(workout: WorkoutModel?) {
        if (workout == null) {
            updateName("")
            updateNotes("")
        } else {
            updateName(workout.name)
            updateNotes(workout.notes)
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

    /**
     * Add/edit the workout if it's valid
     * @param add true to add the workout, false to edit
     */
    fun saveWorkout(add: Boolean) {
        if (!validate()) {
            return
        }

        if (add) {
            viewModelScope.launch(Dispatchers.IO) {
                workoutsRepository.addWorkout(
                    workout = WorkoutModel(0, _uiState.value.name, false, mutableListOf(), _uiState.value.notes, null, 0),
                    onSuccess = { createdWorkout ->
                        onWorkoutActionSuccess(createdWorkout, Page.SelectedWorkout)
                    }
                )
            }
        } else {
            val workout = workoutsRepository.selectedWorkout.value!!
            workout.name = _uiState.value.name
            workout.notes = _uiState.value.notes

            viewModelScope.launch(Dispatchers.IO) {
                workoutsRepository.updateWorkout (
                    workout = workout,
                    onSuccess = { updatedWorkout ->
                        onWorkoutActionSuccess(updatedWorkout, Page.SelectedWorkout)
                    }
                )
            }
        }
    }

    /** Delete the workout */
    fun deleteWorkout() {
        viewModelScope.launch {
            AskQuestionDialogManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.DELETE_WORKOUT,
                show = true,
                onCancel = {
                    viewModelScope.launch {
                        AskQuestionDialogManager.hideQuestion()
                    }
                },
                onConfirm = {
                    viewModelScope.launch(Dispatchers.IO) {
                        workoutsRepository.deleteWorkout(
                            workoutId = workoutsRepository.selectedWorkout.value!!.id,
                            onSuccess = {
                                viewModelScope.launch {
                                    AskQuestionDialogManager.hideQuestion()
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
     * Execute the logic when workout CRUD operation has been executed successfully
     * @param workout the workout to set as selected, may be null
     * @param redirectToPage the page to redirect to
     */
    private fun onWorkoutActionSuccess(workout: WorkoutModel?, redirectToPage: Page) {
        workoutsRepository.updateSelectedWorkout(workout)

        viewModelScope.launch(Dispatchers.IO) {
            workoutsRepository.updateWorkouts(null)

            withContext(Dispatchers.Main) {
                DialogManager.hideDialog("AddEditWorkoutDialog")
                PagerManager.changePageSelection(redirectToPage)
            }
        }
    }

    /** Validate the fields in the UI, return true if valid, false otherwise */
    private fun validate(): Boolean {
        if (_uiState.value.name.isEmpty()) {
            updateNameError(resourceProvider.getString(R.string.error_msg_enter_workout_name))
            return false
        } else {
            updateNameError(null)
        }

        return true
    }
}