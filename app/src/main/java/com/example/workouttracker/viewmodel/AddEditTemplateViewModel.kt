package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.viewmodel.AddEditWorkoutViewModel.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** View model to control the UI state of Add / Edit template dialog */
@HiltViewModel
class AddEditTemplateViewModel @Inject constructor(
    private var resourceProvider: ResourceProvider,
    private var workoutRepository: WorkoutRepository,
    private var workoutTemplatesRepository: WorkoutTemplatesRepository,
    private val vibrationManager: VibrationManager,
    private val dialogManager: DialogManager,
    private val pagerManager: PagerManager
): ViewModel() {

    /** Class containing all fields in the UI */
    data class UIState(
        val name: String = "",
        val notes: String = "",
        val nameError: String? = null
    )

    /** Dialog state */
    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    /** The dialog mode */
    private var mode = Mode.ADD

    /**
     * Update the UI sate to empty upon dialog recreation, as the view model is
     * HiltViewModel and is created only once per activity lifetime
     * @param template the template workout
     * @param dialogMode the dialog mode (Add / Edit)
     */
    fun initialize(template: WorkoutModel, dialogMode: Mode) {
        mode = dialogMode
        updateName(template.name)
        updateNotes(template.notes)
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

    /** Add/edit the template if it's valid */
    fun saveTemplate() {
        if (!validate()) {
            return
        }

        if (mode == Mode.ADD) {
            addTemplate()
        } else {
            editTemplate()
        }
    }

    /** Validate the fields in the UI, return true if valid, false otherwise */
    private fun validate(): Boolean {
        if (_uiState.value.name.isEmpty()) {
            viewModelScope.launch { vibrationManager.makeVibration() }
            updateNameError(resourceProvider.getString(R.string.error_msg_enter_template_name))
            return false
        } else {
            updateNameError(null)
        }

        return true
    }

    /** Add the new template */
    private fun addTemplate() {
        // Create template, changing the name and notes
        val template = WorkoutModel(idVal = 0, nameVal = _uiState.value.name, templateVal = true,
                                    exercisesVal = workoutRepository.selectedWorkout.value!!.exercises,
                                    notesVal = uiState.value.notes, finishDateTimeVal = null, durationVal = null
        )

        // Mark all sets as uncompleted
        template.exercises.map { e ->
            e.sets.map { it.completed = false }
        }

        viewModelScope.launch(Dispatchers.IO) {
            workoutTemplatesRepository.addWorkoutTemplate(
                template = template,
                onSuccess = {
                    viewModelScope.launch {
                        dialogManager.hideDialog("AddEditTemplateDialog")
                        pagerManager.changePageSelection(Page.ManageTemplates)
                    }
                }
            )
        }
    }

    /** Edit the template */
    private fun editTemplate() {

    }
}