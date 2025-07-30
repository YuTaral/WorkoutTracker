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
import com.example.workouttracker.viewmodel.ManageTeamsViewModel.ViewTeamAs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.Question

/** View model to control the state of assign workout screen */
@HiltViewModel
class AssignWorkoutViewModel @Inject constructor(
    var teamRepository: TeamRepository,
    var templatesRepository: WorkoutTemplatesRepository,
    private var resourceProvider: ResourceProvider,
    private var snackbarManager: SnackbarManager,
    private var vibrationManager: VibrationManager,
    private var askQuestionManager: AskQuestionDialogManager,
    private var pagerManager: PagerManager
): ViewModel() {

    /** The screen modes */
    enum class Mode {
        SELECT_TEAM,
        SELECT_MEMBERS,
        SELECT_WORKOUT
    }

    /** Variable to keep track of the screen size */
    private var _mode = MutableStateFlow(Mode.SELECT_TEAM)
    var mode = _mode.asStateFlow()

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
                viewModelScope.launch(Dispatchers.IO) {
                    templatesRepository.refreshTemplates()
                }
            }
        }

        _mode.value = mode
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

    /** Select the workout template and ask for assign confirmation*/
    fun selectWorkoutTemplate(template: WorkoutModel) {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.ASSIGN_WORKOUT,
                onConfirm = { assignWorkout(template.id) },
                formatQValues = listOf(template.name),
            ))
        }
    }

    /**
     * Send request to assign workout to the members
     * @param id the workout id (template)
     */
    fun assignWorkout(id: Long) {
        val selectedMembers = teamRepository.teamMembers.value.filter { it.selectedForAssign }.map { it.id }

        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.assignWorkout(
                workoutId = id,
                memberIds = selectedMembers,
                onSuccess = {
                    viewModelScope.launch {
                        pagerManager.changePageSelection(Page.AssignedWorkouts)
                    }
                }
            )
        }
    }
}
