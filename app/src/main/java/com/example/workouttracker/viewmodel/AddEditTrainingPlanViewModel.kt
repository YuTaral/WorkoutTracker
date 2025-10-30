package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TrainingProgramModel
import com.example.workouttracker.data.network.repositories.TrainingProgramRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** View model to control the UI state of Training Program screen */
@HiltViewModel
class AddEditTrainingPlanViewModel @Inject constructor(
    var trainingProgramRepository: TrainingProgramRepository,
    private var dialogManager: DialogManager,
    private var resourceProvider: ResourceProvider,
    private var vibrationManager: VibrationManager,
    private var pagerManager: PagerManager,
    private var askQuestionManager: AskQuestionDialogManager
): ViewModel() {

    /** Class representing the UI state fields */
    data class UIState(
        val name: String = "",
        val description: String = "",
        val nameError: String? = null,
    )

    /** Screen state */
    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    /** Update the name in the UI with the provided value */
    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    /** Update the description in the UI with the provided value */
    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    /** Update the name error in the UI with the provided value */
    fun updateNameError(value: String?) {
        _uiState.update { it.copy(nameError = value) }
    }

    /** Initialize the data when the screen is shown */
    fun initializeData() {
        if (trainingProgramRepository.selectedTrainingProgram.value != null) {
            updateName(trainingProgramRepository.selectedTrainingProgram.value!!.name)
            updateDescription(trainingProgramRepository.selectedTrainingProgram.value!!.description)
        } else {
            updateName("")
            updateDescription("")
        }

        updateNameError(null)
    }

    /** Save the new training plan / edit the selected one */
    fun save() {
        if (!validate()) {
            return
        }

        val trainingProgram = TrainingProgramModel(0, _uiState.value.name, _uiState.value.description)

        viewModelScope.launch(Dispatchers.IO) {
            if (trainingProgramRepository.selectedTrainingProgram.value == null) {
                // Add new training program
                trainingProgramRepository.addTrainingProgram(
                    trainingProgram = trainingProgram,
                    onSuccess = {
                        viewModelScope.launch {
                            // On success, auto select the created training program and navigate to its details page
                            trainingProgramRepository.updateSelectedTrainingProgram(it)
                            pagerManager.changePageSelection(Page.SelectedTrainingPlan)
                            dialogManager.hideDialog("AddEditTrainingPlanDialog")
                        }
                    }
                )
            } else {
                trainingProgram.id = trainingProgramRepository.selectedTrainingProgram.value!!.id

                trainingProgramRepository.updateTrainingProgram(
                    trainingProgram = trainingProgram,
                    onSuccess = {
                        viewModelScope.launch {
                            // On success, auto select the updated training program
                            trainingProgramRepository.updateSelectedTrainingProgram(it)
                            dialogManager.hideDialog("AddEditTrainingPlanDialog")
                        }
                    }
                )
            }
        }
    }

    /** Delete the selected training program */
    fun delete() {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.DELETE_TRAINING_PLAN,
                onConfirm = {
                    viewModelScope.launch(Dispatchers.IO) {
                        trainingProgramRepository.deleteTrainingProgram(
                            trainingProgramId = trainingProgramRepository.selectedTrainingProgram.value!!.id,
                            onSuccess = {
                                viewModelScope.launch {
                                    trainingProgramRepository.updateSelectedTrainingProgram(null)
                                    pagerManager.changePageSelection(Page.ManageTrainingPlans)
                                    dialogManager.hideDialog("AddEditTrainingPlanDialog")
                                }
                            }
                        )
                    }
                },
                formatQValues = listOf(trainingProgramRepository.selectedTrainingProgram.value!!.name)
            ))
        }
    }

    /** Validate the fields in the UI, return true if valid, false otherwise */
    private fun validate(): Boolean {
        if (_uiState.value.name.isEmpty()) {
            viewModelScope.launch { vibrationManager.makeVibration() }
            updateNameError(resourceProvider.getString(R.string.error_msg_enter_training_program_name))
            return false
        } else {
            updateNameError(null)
        }

        return true
    }

}