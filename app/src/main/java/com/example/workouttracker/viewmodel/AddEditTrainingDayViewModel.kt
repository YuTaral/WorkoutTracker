package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.TrainingDayModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.TrainingPlanRepository
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** View model to control the UI state of Training Program screen */
@HiltViewModel
class AddEditTrainingDayViewModel @Inject constructor(
    var templatesRepository: WorkoutTemplatesRepository,
    private var trainingProgramRepository: TrainingPlanRepository,
    private var dialogManager: DialogManager,
    private var askQuestionManager: AskQuestionDialogManager,
): ViewModel() {

    /** The state of each template row */
    data class TemplateUIState(
        val template: WorkoutModel,
        val selected: Boolean = false
    )

    /** Class containing all fields and information in the UI */
    data class UIState(
        val trainingDay: TrainingDayModel = TrainingDayModel(0),
        val templatesState: MutableList<TemplateUIState> = mutableListOf(),
    )

    /** UI state of the dialog */
    private var _uiState = MutableStateFlow(UIState())
    var uiState = _uiState.asStateFlow()

    /** Initialize the data when the dialog is shown */
    fun initializeData(model: TrainingDayModel) {
        val templatesStateList = mutableListOf<TemplateUIState>()

        for (template in templatesRepository.templates.value) {
            val isSelected = model.workouts.any { it.id == template.id }
            templatesStateList.add(TemplateUIState(template = template, selected = isSelected))
        }

        _uiState.update {
            it.copy(
                trainingDay = model,
                templatesState = templatesStateList
            )
        }
    }

    /**
     * Mark the template as selected/unselected for the day
     * @param template the template to add
     */
    fun changeTemplateSelected(template: TemplateUIState) {
        _uiState.update {
            // Change the selected state of the template
            val updatedSelectedTemplates = it.templatesState.toMutableList()
            val index = templatesRepository.templates.value.indexOfFirst { t -> t.id == template.template.id }
            updatedSelectedTemplates[index] = updatedSelectedTemplates[index].copy(selected = !template.selected)

            it.copy(templatesState = updatedSelectedTemplates)
        }

    }

    /**
     * Save the changes to the training day
     */
    fun save() {
        _uiState.update {
            // Update the training day workouts based on selected templates
            var trainingDay = it.trainingDay
            trainingDay.workouts = it.templatesState.filter { t -> t.selected }.map { t -> t.template }.toMutableList()
            it.copy(
                trainingDay = trainingDay
            )
        }

        if (_uiState.value.trainingDay.id == 0L) {
            createTrainingDay()
        } else {
            updateTrainingDay()
        }
    }

    /**
     * Ask for confirmation to delete the training day
     */
    fun askDelete() {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.DELETE_TRAINING_DAY,
                onConfirm = {
                    viewModelScope.launch(Dispatchers.IO) {
                        trainingProgramRepository.deleteTrainingDay(
                            trainingDayId = _uiState.value.trainingDay.id ,
                            onSuccess = {
                                viewModelScope.launch {
                                    trainingProgramRepository.updateSelectedTrainingPlan(it)
                                    dialogManager.hideDialog("AddEditTrainingDayDialog")
                                }
                            }
                        )
                    }
                }
            ))
        }
    }

    /** Send request to add new training day to the program */
    private fun createTrainingDay() {
        viewModelScope.launch(Dispatchers.IO) {
            trainingProgramRepository.addTrainingDayToPlan(
                trainingDay = _uiState.value.trainingDay,
                onSuccess = {
                    viewModelScope.launch {
                        dialogManager.hideDialog("AddEditTrainingDayDialog")
                        trainingProgramRepository.updateSelectedTrainingPlan(it)
                    }
                }
            )
        }
    }

    /** Send request to update the training day */
    private fun updateTrainingDay() {
        viewModelScope.launch(Dispatchers.IO) {
            trainingProgramRepository.updateTrainingDayToPlan(
                trainingDay = _uiState.value.trainingDay,
                onSuccess = {
                    viewModelScope.launch {
                        dialogManager.hideDialog("AddEditTrainingDayDialog")
                        trainingProgramRepository.updateSelectedTrainingPlan(it)
                    }
                }
            )
        }
    }
}