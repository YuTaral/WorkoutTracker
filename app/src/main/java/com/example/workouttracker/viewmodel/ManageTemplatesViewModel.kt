package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository
import com.example.workouttracker.ui.components.dialogs.AddEditWorkoutDialog
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Enum representing the actions from the action spinner */
enum class TemplateSpinnerActions(private val stringId: Int) {
    START_WORKOUT(R.string.action_start_workout),
    EDIT_TEMPLATE(R.string.action_edit_template),
    DELETE_TEMPLATE(R.string.action_delete_template);

    fun getStringId(): Int {
        return stringId
    }
}

@HiltViewModel
class ManageTemplatesViewModel @Inject constructor(
    var userRepository: UserRepository,
    private var templatesRepository: WorkoutTemplatesRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** Use job with slight delay to avoid filtering the data on each letter */
    private val debounceTime = 500L
    private var searchJob: Job? = null

    /** The templates of the user */
    private var _filteredTemplates = MutableStateFlow<MutableList<WorkoutModel>>(mutableListOf())
    var filteredTemplates = _filteredTemplates.asStateFlow()

    /** The search term for templates */
    private var _search = MutableStateFlow<String>("")
    var search = _search.asStateFlow()

    /** Valid Spinner actions */
    var spinnerActions: List<TemplateSpinnerActions> = listOf(
        TemplateSpinnerActions.START_WORKOUT,
        TemplateSpinnerActions.EDIT_TEMPLATE,
        TemplateSpinnerActions.DELETE_TEMPLATE,
    )

    /** The selected spinner action  */
    private var _selectedSpinnerAction = MutableStateFlow<TemplateSpinnerActions>(TemplateSpinnerActions.START_WORKOUT)
    var selectedSpinnerAction = _selectedSpinnerAction.asStateFlow()

    /** Initialize the data in the panel */
    fun initializeData() {
        viewModelScope.launch {
            templatesRepository.templates.collect { newTemplates ->
                _filteredTemplates.value = if (_search.value.isEmpty()) {
                    newTemplates
                } else {
                    newTemplates.filter {
                        it.name.contains(_search.value, ignoreCase = true)
                    }.toMutableList()
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            templatesRepository.refreshTemplates()
        }
    }

    /** Filter the muscle groups when the search value changes */
    fun updateSearch(value: String) {
        _search.value = value

        searchJob?.cancel()

        searchJob = viewModelScope.launch(Dispatchers.Default) {
            // Wait for the debounce time before filtering to avoid filtering on each letter
            delay(debounceTime)

            if (value.isEmpty()) {
                _filteredTemplates.value = templatesRepository.templates.value
            } else {
                _filteredTemplates.value = templatesRepository.templates.value.filter {
                    it.name.contains(value, ignoreCase = true)
                } as MutableList<WorkoutModel>
            }
        }
    }

    /** Update the selected spinner action with the provided value */
    fun updateSelectedSpinnerAction(actionText: String) {
        _selectedSpinnerAction.value = spinnerActions.first {
            resourceProvider.getString(it.getStringId()) == actionText
        }
    }

    /** Select the template and execute the action based on the selected action */
    fun selectTemplate(template: WorkoutModel) {
        when(_selectedSpinnerAction.value) {
            TemplateSpinnerActions.START_WORKOUT -> {
                showStartWorkout(template)
            }
            TemplateSpinnerActions.EDIT_TEMPLATE -> {
                showEditTemplate(template)
            }
            TemplateSpinnerActions.DELETE_TEMPLATE -> {
                askDeleteTemplate(template)
            }
        }
    }

    /**
     * Show start workout dialog with the selected template
     * @param template the selected template
     */
    private fun showStartWorkout(template: WorkoutModel) {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.add_workout_title),
                dialogName = "AddEditWorkoutDialog",
                content = { AddEditWorkoutDialog(workout = template, mode = AddEditWorkoutModel.ADD) }
            )
        }
    }

    /**
     * Show edit workout dialog with the selected template
     * @param template the selected template
     */
    private fun showEditTemplate(template: WorkoutModel) {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.edit_template_title),
                dialogName = "AddEditWorkoutDialog",
                content = { AddEditWorkoutDialog(workout = template, mode = AddEditWorkoutModel.EDIT) }
            )
        }
    }

    /**
     * Ask user for confirmation to delete the template
     *  @param template the selected template
     */
    private fun askDeleteTemplate(template: WorkoutModel) {
        viewModelScope.launch {
            AskQuestionDialogManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.DELETE_TEMPLATE,
                show = true,
                onCancel = {
                    viewModelScope.launch {
                        AskQuestionDialogManager.hideQuestion()
                    }
                },
                onConfirm = {
                    viewModelScope.launch(Dispatchers.IO) {
                        templatesRepository.deleteWorkoutTemplate(
                            id = template.id,
                            onSuccess = {
                                viewModelScope.launch {
                                    AskQuestionDialogManager.hideQuestion()
                                }
                            }
                        )
                    }
                },
                formatQValues = listOf(template.name)
            ))
        }
    }
}