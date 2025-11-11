package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.dialogs.AddEditTemplateDialog
import com.example.workouttracker.ui.dialogs.StartTimerDialog
import com.example.workouttracker.ui.dialogs.TimerDialog
import com.example.workouttracker.ui.managers.CustomNotificationManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.LoadingManager
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.utils.Constants.ViewTeamAs
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.viewmodel.Action.SaveWorkoutAsTemplate
import com.example.workouttracker.viewmodel.AddEditWorkoutViewModel.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Different actions accessed from the actions menu */
sealed class Action(val imageId: Int, val titleId: Int, val onClick: suspend () -> Unit ) {
    data class ManageExercises(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_screen_manage_exercises, R.string.manage_exercises_lbl, { onActionClick() }
    )

    data class ManageTemplates(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_screen_manage_templates, R.string.manage_templates_lbl, { onActionClick() }
    )

    data class ManageTeams(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_screen_manage_teams, R.string.manage_teams_lbl, { onActionClick() }
    )

    data class FinishWorkout(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_finish_workout, R.string.mark_workout_as_finished_lbl, { onActionClick() }
    )

    data class StartTimer(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_start_timer, R.string.start_timer_lbl, { onActionClick() }
    )

    data class SaveWorkoutAsTemplate(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_action_save_workout_as_template, R.string.save_workout_as_template_lbl, { onActionClick() }
    )

    data class AssignWorkout(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_assign_workout, R.string.assign_workout_action, { onActionClick() }
    )

    data class TrainingPlan(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_screen_training_plan, R.string.manage_training_plan_action, { onActionClick() }
    )

    data class AssignedWorkouts(private val onActionClick: () -> Unit):
        Action(R.drawable.icon_screen_workouts, R.string.view_assigned_workouts_action, { onActionClick() }
    )
}

/** View model to manage the state of Select Action screen */
@HiltViewModel
class SelectActionViewModel @Inject constructor(
    private var workoutRepository: WorkoutRepository,
    private var resourceProvider: ResourceProvider,
    private var notificationManager: CustomNotificationManager,
    private val dialogManager: DialogManager,
    private val loadingManager: LoadingManager,
    private val pagerManager: PagerManager
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
            _actions.value.add(createSaveWorkoutAsTemplate())

            if (workoutRepository.selectedWorkout.value!!.finishDateTime == null) {
                _actions.value.add(createFinishWorkout())
            }
        }

        _actions.value.add(createStartTimerAction())
        _actions.value.add(createManageExercisesAction())
        _actions.value.add(createManageTemplates())
        _actions.value.add(createManageTeamsAction())
        _actions.value.add(createAssignAction())
        _actions.value.add(createManageTrainingPlanAction())
        _actions.value.add(createViewAssignedWorkoutsAction())

        _isInitialized.value = true
    }

    /** Create save workout as template action */
    private fun createSaveWorkoutAsTemplate(): Action {
        val template = workoutRepository.selectedWorkout.value!!

        return SaveWorkoutAsTemplate(
            onActionClick = {
                viewModelScope.launch {
                    dialogManager.showDialog(
                        title = resourceProvider.getString(R.string.add_template_lbl),
                        dialogName = "AddEditTemplateDialog",
                        content = { AddEditTemplateDialog(template = template, mode = Mode.ADD) }
                    )
                }
            },
        )
    }

    /** Create finish workout action */
    private fun createFinishWorkout(): Action {
        return Action.FinishWorkout(onActionClick = {
            viewModelScope.launch(Dispatchers.IO) {
                workoutRepository.finishWorkout(
                    workoutId = workoutRepository.selectedWorkout.value!!.id,
                    onSuccess = {
                        workoutRepository.updateSelectedWorkout(it)
                        viewModelScope.launch(Dispatchers.IO) {
                            workoutRepository.updateWorkouts(null, null)

                            withContext(Dispatchers.Default) {
                                pagerManager.changePageSelection(Page.SelectedWorkout)
                            }
                        }
                    }
                )
            }
        })
    }

    /** Create start timer action */
    private fun createStartTimerAction(): Action {
        return Action.StartTimer(
            onActionClick = {
                viewModelScope.launch {
                    dialogManager.showDialog(
                        title = resourceProvider.getString(R.string.start_timer_lbl),
                        dialogName = "StartTimerDialog",
                        content = {
                            StartTimerDialog(onStart = {
                                viewModelScope.launch {
                                    dialogManager.showDialog(
                                        title = resourceProvider.getString(R.string.timer_lbl),
                                        dialogName = "TimerDialog",
                                        content = {
                                            TimerDialog(
                                                seconds = it,
                                                autoStart = true,
                                                onDone = {
                                                    viewModelScope.launch {
                                                        dialogManager.hideDialog("StartTimerDialog")
                                                        dialogManager.hideDialog("TimerDialog")
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
                            })
                        }
                    )
                }
            }
        )
    }

    /** Create manage teams action */
    private fun createManageTeamsAction(): Action {
        return Action.ManageTeams(
            onActionClick = {
                viewModelScope.launch {
                    pagerManager.changePageSelection(Page.ManageTeams(teamType = ViewTeamAs.COACH))
                }
            }
        )
    }

    /** Create manage templates action */
    private fun createManageTemplates(): Action {
        return Action.ManageTemplates(
            onActionClick = {
                viewModelScope.launch {
                    pagerManager.changePageSelection(Page.ManageTemplates)
                }
            }
        )
    }

    /** Create manage exercises action */
    private fun createManageExercisesAction(): Action {
        return Action.ManageExercises(
            onActionClick = {
                viewModelScope.launch {
                    pagerManager.changePageSelection(Page.ManageExercise)
                }
            }
        )
    }

    /** Create assign workout action */
    private fun createAssignAction(): Action {
        return Action.AssignWorkout(
            onActionClick = {
                viewModelScope.launch {
                    pagerManager.changePageSelection(Page.AssignWorkout)
                }
            }
        )
    }

    /** Create training plan action */
    private fun createManageTrainingPlanAction(): Action {
        return Action.TrainingPlan(
            onActionClick = {
                viewModelScope.launch {
                    pagerManager.changePageSelection(Page.ManageTrainingPlans)
                }
            }
        )
    }

    /** Create assign workout action */
    private fun createViewAssignedWorkoutsAction(): Action {
        return Action.AssignedWorkouts(
            onActionClick = {
                viewModelScope.launch {
                    pagerManager.changePageSelection(Page.AssignedWorkouts)
                }
            }
        )
    }

    /** Reset the data when the screen is being removed */
    fun resetData() {
        _actions.value.clear()
        _isInitialized.value = false
    }

    /**
     * Show / hide the loading dialog
     * @param show true to show it, false to hide it
     */
    fun showHideLoading(show: Boolean) {
        if (show) {
            viewModelScope.launch {
                loadingManager.showLoading()
            }
        } else {
            viewModelScope.launch {
                loadingManager.hideLoading()
            }
        }
    }
}