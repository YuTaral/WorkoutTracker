package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.dialogs.AddEditTemplateDialog
import com.example.workouttracker.ui.dialogs.StartTimerDialog
import com.example.workouttracker.ui.dialogs.TimerDialog
import com.example.workouttracker.ui.managers.CustomNotificationManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.viewmodel.AddEditWorkoutViewModel.Mode
import com.example.workouttracker.viewmodel.ManageTeamsViewModel.ViewTeamAs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.util.Date
import javax.inject.Inject

/** Different actions accessed from the actions menu */
sealed class Action(val imageId: Int, val titleId: Int, val onClick: suspend () -> Unit ) {
    data object ManageExercises : Action(R.drawable.icon_screen_manage_exercises, R.string.manage_exercises_lbl,
        { PagerManager.changePageSelection(Page.ManageExercise) })

    data object ManageTemplates : Action(R.drawable.icon_screen_manage_templates, R.string.manage_templates_lbl,
        { PagerManager.changePageSelection(Page.ManageTemplates) })

    data object ManageTeams : Action(R.drawable.icon_screen_manage_teams, R.string.manage_teams_lbl,
        { PagerManager.changePageSelection(Page.ManageTeams(teamType = ViewTeamAs.COACH)) })

    data class FinishWorkout(private val onActionClick: () -> Unit): Action(R.drawable.icon_finish_workout, R.string.mark_workout_as_finished_lbl, { onActionClick() })

    data class StartTimer(private val title: String, private val showTimer: (Long) -> Unit):
        Action(R.drawable.icon_start_timer, R.string.start_timer_lbl,
        {
            DialogManager.showDialog(
                title = title,
                dialogName = "StartTimerDialog",
                content = { StartTimerDialog(onStart = { showTimer(it) }) }
            )
        }
    )

    data class SaveWorkoutAsTemplate(private val template: WorkoutModel, private val title: String):
        Action(R.drawable.icon_action_save_workout_as_template, R.string.save_workout_as_template_lbl,
        {
            DialogManager.showDialog(
                title = title,
                dialogName = "AddEditTemplateDialog",
                content = { AddEditTemplateDialog(template = template, mode = Mode.ADD) }
            )
        }
    )
}

/** View model to manage the state of Select Action screen */
@HiltViewModel
class SelectActionViewModel @Inject constructor(
    private var workoutRepository: WorkoutRepository,
    private var resourceProvider: ResourceProvider,
    private var notificationManager: CustomNotificationManager
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

            if (workoutRepository.selectedWorkout.value!!.finishDateTime == null) {
                _actions.value.add(Action.FinishWorkout(onActionClick = {
                    val workout = workoutRepository.selectedWorkout.value!!
                    workout.finishDateTime = Date()
                    workout.durationSeconds = Duration.between(
                        workout.startDateTime!!.toInstant(), workout.finishDateTime!!.toInstant()
                    ).seconds.toInt()

                    viewModelScope.launch(Dispatchers.IO) {
                        workoutRepository.updateWorkout(
                            workout = workout,
                            onSuccess = {
                                workoutRepository.updateSelectedWorkout(it)
                                viewModelScope.launch(Dispatchers.IO) {
                                    workoutRepository.updateWorkouts(null)

                                    withContext(Dispatchers.Default) {
                                        PagerManager.changePageSelection(Page.SelectedWorkout)
                                    }
                                }
                            }
                        )
                    }
                }))
            }
        }

        _actions.value.add(Action.StartTimer(
            title = resourceProvider.getString(R.string.start_timer_lbl),
            showTimer = {
                viewModelScope.launch {
                    DialogManager.showDialog(
                        title = resourceProvider.getString(R.string.timer_lbl),
                        dialogName = "TimerDialog",
                        content = {
                            TimerDialog(
                                seconds = it,
                                autoStart = true,
                                onDone = {
                                    viewModelScope.launch {
                                        DialogManager.hideDialog("StartTimerDialog")
                                        DialogManager.hideDialog("TimerDialog")
                                    }
                                },
                                sendNotification = {
                                    notificationManager.sendNotification(
                                        context = it,
                                        titleId = R.string.time_is_up_lbl,
                                        messageId = R.string.time_finished_lbl
                                    )
                                }
                            )
                        }
                    )
                }
            }),
        )

        _actions.value.add(Action.ManageExercises)
        _actions.value.add(Action.ManageTemplates)
        _actions.value.add(Action.ManageTeams)

        _isInitialized.value = true
    }

    /** Reset the data when the screen is being removed */
    fun resetData() {
        _actions.value.clear()
        _isInitialized.value = false
    }
}