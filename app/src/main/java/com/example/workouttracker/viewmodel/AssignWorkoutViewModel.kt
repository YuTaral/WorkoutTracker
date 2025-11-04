package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.TeamMemberModel
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.ui.components.MemberTeamState
import com.example.workouttracker.ui.managers.SnackbarManager
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.workouttracker.R
import com.example.workouttracker.data.models.BaseModel
import com.example.workouttracker.data.models.TrainingPlanModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.TrainingPlanRepository
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DatePickerDialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.utils.Constants.ViewTeamAs
import java.util.Calendar
import java.util.Date

/** View model to control the state of assign workout screen */
@HiltViewModel
class AssignWorkoutViewModel @Inject constructor(
    var teamRepository: TeamRepository,
    var templatesRepository: WorkoutTemplatesRepository,
    var trainingPlanRepository: TrainingPlanRepository,
    private var resourceProvider: ResourceProvider,
    private var snackbarManager: SnackbarManager,
    private var vibrationManager: VibrationManager,
    private var askQuestionManager: AskQuestionDialogManager,
    private var pagerManager: PagerManager,
    private var datePickerDialog: DatePickerDialogManager,
): ViewModel() {

    /** The screen modes */
    enum class Mode {
        SELECT_TEAM,
        SELECT_MEMBERS,
        SELECT_WORKOUT
    }

    /** Enum with types of workout selection */
    enum class WorkoutSelection(private val stringId: Int) {
        SINGLE_WORKOUT(R.string.single_workout_selection),
        TRAINING_PLAN(R.string.training_plan_selection);

        fun getStringId(): Int {
            return stringId
        }
    }

    /** Variable to keep track screen mode*/
    private var _mode = MutableStateFlow(Mode.SELECT_TEAM)
    var mode = _mode.asStateFlow()

    /** Track the workout selection type*/
    private var _selectedWorkoutSelection = MutableStateFlow<WorkoutSelection>(WorkoutSelection.SINGLE_WORKOUT)
    var selectedWorkoutSelection = _selectedWorkoutSelection.asStateFlow()

    /** Selected workouts start date */
    private var _startDate = MutableStateFlow(getDefaultStartDate())
    var startDate = _startDate.asStateFlow()

    /** Update the selected mode with the provided value */
    fun updateSelectedMode(mode: Mode) {
        if (mode == Mode.SELECT_WORKOUT) {
            // Validate there are selected members
            if (teamRepository.teamMembers.value.none { it.selectedForAssign }) {
                viewModelScope.launch {
                    snackbarManager.showSnackbar(resourceProvider.getString(R.string.no_members_selected))
                    vibrationManager.makeVibration()
                }
                return
            } else {
                if (selectedWorkoutSelection.value == WorkoutSelection.TRAINING_PLAN) {
                    // Load training plans
                    viewModelScope.launch(Dispatchers.IO) {
                        trainingPlanRepository.refreshTrainingPlans()
                    }
                } else {
                    // Load workout templates
                    viewModelScope.launch(Dispatchers.IO) {
                        templatesRepository.refreshTemplates()
                    }
                }
            }
       }
        _mode.value = mode
    }

    /** Update the selected workout selection */
    fun updateSelectedWorkoutSelection(value: String) {
        val matchingEnum = WorkoutSelection.entries.firstOrNull { enum ->
            resourceProvider.getString(enum.getStringId()) == value
        }

        if (matchingEnum != null) {
            _selectedWorkoutSelection.value = matchingEnum

            if (selectedWorkoutSelection.value == WorkoutSelection.TRAINING_PLAN) {
                // Load training plans
                viewModelScope.launch(Dispatchers.IO) {
                    trainingPlanRepository.refreshTrainingPlans()
                }
            } else {
                // Load workout templates
                viewModelScope.launch(Dispatchers.IO) {
                    templatesRepository.refreshTemplates()
                }
            }
        }
    }

    /**
     * Update the workouts start date
     * @param newDate the new start date
     */
    private fun updateStartDate(newDate: Date) {
        _startDate.value = newDate
    }

    /** Initialize the data when the screen is shown */
    fun initializeData() {
        updateSelectedMode(Mode.SELECT_TEAM)

        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.refreshMyTeams(
                teamType = ViewTeamAs.COACH.name,
                onlyWithMembers = true
            )
            teamRepository.updateSelectedTeam(null)
        }
    }

    /** Update the selected team */
    fun selectTeam(team: TeamModel?) {
        viewModelScope.launch {
            teamRepository.updateSelectedTeam(team)
        }

        if (team == null) {
            updateSelectedMode(Mode.SELECT_TEAM)
        } else {
            updateSelectedMode(Mode.SELECT_MEMBERS)
        }
    }

     /** Mark the member as selected / unselected for assign */
     fun selectMember(model: TeamMemberModel) {
         if (model.teamState == MemberTeamState.ACCEPTED.name) {
             teamRepository.updateMemberSelection(model)
         }
     }

    /** Select the workout template / training plan and ask for assign confirmation */
    fun selectWorkouts(selection: BaseModel) {
        if (selectedWorkoutSelection.value == WorkoutSelection.SINGLE_WORKOUT) {
            selectSingleWorkout(selection as WorkoutModel)
        } else {
            selectTrainingPlan(selection as TrainingPlanModel)
        }
    }

    /** Display the date picker dialog */
    fun showDatePicker() {
        viewModelScope.launch {
            datePickerDialog.showDialog(
                onCancel = {
                    viewModelScope.launch {
                        datePickerDialog.hideDialog()
                    }
                },
                onDatePick = { newDate ->
                    viewModelScope.launch {
                        datePickerDialog.hideDialog()
                    }

                    updateStartDate(newDate)
                }
            )
        }
    }

    /**
     * Send request to assign workout to the members
     * @param id the workout id (template)
     */
    private fun assignWorkout(id: Long) {
        val selectedMembers = teamRepository.teamMembers.value.filter { it.selectedForAssign }.map { it.id }

        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.assignWorkout(
                workoutId = id,
                memberIds = selectedMembers,
                startDate = _startDate.value,
                onSuccess = {
                    viewModelScope.launch {
                        pagerManager.changePageSelection(Page.AssignedWorkouts)
                    }
                }
            )
        }
    }

    /**
     * Send request to assign workout to the members
     * @param trainingPlanId the training plan id
     */
    private fun assignTrainingPlan(trainingPlanId: Long) {
        val selectedMembers = teamRepository.teamMembers.value.filter { it.selectedForAssign }.map { it.id }

        viewModelScope.launch(Dispatchers.IO) {
            trainingPlanRepository.assignTrainingPlan(
                trainingPlanId = trainingPlanId,
                memberIds = selectedMembers,
                onSuccess = {
                    viewModelScope.launch {
                        pagerManager.changePageSelection(Page.AssignedWorkouts)
                    }
                }
            )
        }
    }

    /** Select single workout and ask for assignment confirmation */
    private fun selectSingleWorkout(selection: WorkoutModel) {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.ASSIGN_WORKOUT,
                onConfirm = { assignWorkout(selection.id) },
                formatQValues = listOf(selection.name),
            ))
        }
    }

    /** Select training plan and ask for assignment confirmation */
    private fun selectTrainingPlan(selection: TrainingPlanModel) {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.ASSIGN_WORKOUT,
                onConfirm = { assignTrainingPlan(selection.id) },
                formatQValues = listOf(selection.name),
            ))
        }
    }

    /** Return the default workouts start date - today */
    private fun getDefaultStartDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }
}
