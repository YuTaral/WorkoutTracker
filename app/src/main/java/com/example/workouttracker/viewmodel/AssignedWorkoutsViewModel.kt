package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.AssignedWorkoutModel
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.ui.managers.DatePickerDialogManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.ManageTeamsViewModel.ViewTeamAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import com.example.workouttracker.R
import com.example.workouttracker.ui.managers.PagerManager
import dagger.hilt.android.lifecycle.HiltViewModel

/** View model to control the state of assigned workouts */
@HiltViewModel
class AssignedWorkoutsViewModel @Inject constructor(
    var teamRepository: TeamRepository,
    private var datePickerDialog: DatePickerDialogManager,
    private var resourceProvider: ResourceProvider,
    private var pagerManager: PagerManager
): ViewModel() {

    /** The assigned workouts */
    private var _assignedWorkouts = MutableStateFlow<MutableList<AssignedWorkoutModel>>(mutableListOf())
    var assignedWorkouts = _assignedWorkouts.asStateFlow()

    /** Selected assigned workouts start date */
    private var _startDate = MutableStateFlow(getDefaultStartDate())
    var startDate = _startDate.asStateFlow()

    /** Selected team filter if any */
    private var _teamFilter = MutableStateFlow(getDefaultTeamFilter())
    var teamFilter = _teamFilter.asStateFlow()

    /**
     * Initialize the data when the screen is displayed
     * @param team the initially selected team, null if not used
     */
    fun initializeData(team: TeamModel?) {
        if (team == null && _teamFilter.value.id == 0L) {
            _teamFilter.value = getDefaultTeamFilter()
        } else if (team != null) {
            _teamFilter.value = team
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (_assignedWorkouts.value.isEmpty()) {
                teamRepository.getAssignedWorkouts(
                    startDate = Utils.formatDateToISO8601(_startDate.value),
                    teamId = _teamFilter.value.id,
                    onSuccess = { _assignedWorkouts.value = it.toMutableList() },
                    onFail = { _assignedWorkouts.value = mutableListOf() }
                )
            }

            if (teamRepository.teams.value.isEmpty()) {
                teamRepository.refreshMyTeams(teamType = ViewTeamAs.COACH.name)
                teamRepository.teams.value.add(0, getDefaultTeamFilter())
            }
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
     * Handle click on an assigned workout
     * @param assignedWorkout the assigned workout to view
     */
    fun onClick(assignedWorkout: AssignedWorkoutModel) {
        viewModelScope.launch {
            pagerManager.changePageSelection(Page.ViewAssignedWorkout(assignedWorkout = assignedWorkout))
        }
    }

    /**
     * Update the workouts start date
     * @param newDate the new start date
     */
    private fun updateStartDate(newDate: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.getAssignedWorkouts(
                startDate = Utils.formatDateToISO8601(newDate),
                teamId = teamFilter.value.id,
                onSuccess = {
                    _assignedWorkouts.value = it.toMutableList()
                    _startDate.value = newDate
                },
                onFail = {
                    _assignedWorkouts.value = mutableListOf()
                    _startDate.value = newDate
                }
            )
        }
    }

    /**
     * Update the selected team filter
     * @param teamId the team id to filter by
     */
    fun updateTeamFilter(teamId: String) {
        _teamFilter.value = teamRepository.teams.value.firstOrNull { it.id.toString() == teamId } ?: getDefaultTeamFilter()

        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.getAssignedWorkouts(
                startDate = Utils.formatDateToISO8601(_startDate.value),
                teamId = _teamFilter.value.id,
                onSuccess = {
                    _assignedWorkouts.value = it.toMutableList()
                },
                onFail = {
                    _assignedWorkouts.value = mutableListOf()
                }
            )
        }
    }

    /** Return the default assigned workouts start date - 1 month backwards */
    private fun getDefaultStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        return calendar.time
    }

    /** Return the default team filter */
    private fun getDefaultTeamFilter(): TeamModel {
        return TeamModel(0L, "", resourceProvider.getString(R.string.all_teams_lbl), "")
    }

}