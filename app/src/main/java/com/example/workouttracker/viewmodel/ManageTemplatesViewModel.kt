package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository
import com.example.workouttracker.ui.dialogs.AddEditWorkoutDialog
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.SearchHelper
import com.example.workouttracker.viewmodel.AddEditWorkoutViewModel.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageTemplatesViewModel @Inject constructor(
    var userRepository: UserRepository,
    private var templatesRepository: WorkoutTemplatesRepository,
    private var resourceProvider: ResourceProvider,
    private var askQuestionManager: AskQuestionDialogManager
): ViewModel() {

    /** Enum representing the actions from the action spinner */
    enum class SpinnerActions(private val stringId: Int) {
        START_WORKOUT(R.string.action_start_workout),
        EDIT_TEMPLATE(R.string.action_edit_template),
        DELETE_TEMPLATE(R.string.action_delete_template);

        fun getStringId(): Int {
            return stringId
        }
    }

    /**
     * Search helper - uses debounce and job to improve performance and not execute the callback
     * on each new symbol
     */
    val searchHelper = SearchHelper(
        coroutineScope = viewModelScope,
        callback = { onSearch(it) }
    )

    /** The templates of the user */
    private var _filteredTemplates = MutableStateFlow<MutableList<WorkoutModel>>(mutableListOf())
    var filteredTemplates = _filteredTemplates.asStateFlow()

    /** Valid Spinner actions */
    var spinnerActions: List<SpinnerActions> = listOf(
        SpinnerActions.START_WORKOUT,
        SpinnerActions.EDIT_TEMPLATE,
        SpinnerActions.DELETE_TEMPLATE,
    )

    /** The selected spinner action  */
    private var _selectedSpinnerAction = MutableStateFlow<SpinnerActions>(SpinnerActions.START_WORKOUT)
    var selectedSpinnerAction = _selectedSpinnerAction.asStateFlow()

    /** Initialize the data in the panel */
    fun initializeData() {
        viewModelScope.launch {
            templatesRepository.templates.collect { newTemplates ->
                _filteredTemplates.value = if (searchHelper.search.value.isEmpty()) {
                    newTemplates
                } else {
                    newTemplates.filter {
                        it.name.contains(searchHelper.search.value, ignoreCase = true)
                    }.toMutableList()
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            templatesRepository.refreshTemplates()
        }

        searchHelper.updateSearchTerm("")
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
            SpinnerActions.START_WORKOUT -> {
                showStartWorkout(template)
            }
            SpinnerActions.EDIT_TEMPLATE -> {
                showEditTemplate(template)
            }
            SpinnerActions.DELETE_TEMPLATE -> {
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
                content = { AddEditWorkoutDialog(workout = template, mode = Mode.ADD) }
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
                content = { AddEditWorkoutDialog(workout = template, mode = Mode.EDIT) }
            )
        }
    }

    /**
     * Ask user for confirmation to delete the template
     *  @param template the selected template
     */
    private fun askDeleteTemplate(template: WorkoutModel) {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.DELETE_TEMPLATE,
                show = true,
                onConfirm = {
                    viewModelScope.launch(Dispatchers.IO) {
                        templatesRepository.deleteWorkoutTemplate(id = template.id,)
                    }
                },
                formatQValues = listOf(template.name)
            ))
        }
    }

    /**
     * Callback to execute on search
     * @param value the new filter search term
     */
    private fun onSearch(value: String) {
        val filtered = if (value.isEmpty()) {
            templatesRepository.templates.value
        } else {
            templatesRepository.templates.value.filter {
                it.name.contains(value, ignoreCase = true)
            }
        }
        _filteredTemplates.value = filtered as MutableList<WorkoutModel>
    }
}