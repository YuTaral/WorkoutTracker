package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.components.dialogs.AddEditTemplateDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** Different actions accessed from the actions menu */
sealed class Action(val imageId: Int, val titleId: Int, val onClick: suspend () -> Unit ) {
    data object ManageExercises : Action(R.drawable.icon_screen_manage_exercises, R.string.manage_exercises_lbl,
        { PagerManager.changePageSelection(Page.ManageExercise) })

    data object ManageTemplates : Action(R.drawable.icon_screen_manage_templates, R.string.manage_templates_lbl,
        { PagerManager.changePageSelection(Page.ManageTemplates) })

    class SaveWorkoutAsTemplate(private val template: WorkoutModel, private val title: String):
        Action(R.drawable.icon_action_save_workout_as_template, R.string.save_workout_as_template_lbl,
        {
            DialogManager.showDialog(
                title = title,
                dialogName = "AddEditTemplateDialog",
                content = { AddEditTemplateDialog(template = template, mode = AddEditTemplateMode.ADD) }
            )
        }
    )
}

/** View model to manage the state of Select Action screen */
@HiltViewModel
class SelectActionViewModel @Inject constructor(
    private var workoutRepository: WorkoutRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** The valid actions */
    private val _actions = MutableStateFlow<MutableList<Action>>(mutableListOf())
    val actions = _actions.asStateFlow()

    /** Track whether the list is initialized */
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    /** Initialize the valid actions */
    fun initializeData() {
        if (workoutRepository.selectedWorkout.value != null) {
            _actions.value.add(Action.SaveWorkoutAsTemplate(
                template = workoutRepository.selectedWorkout.value!!,
                title = resourceProvider.getString(R.string.add_template_lbl)
            ))
        }

        _actions.value.add(Action.ManageExercises)
        _actions.value.add(Action.ManageTemplates)

        _isInitialized.value = true
    }

    /** Reset the data when the screen is being removed */
    fun resetData() {
        _actions.value.clear()
        _isInitialized.value = false
    }
}