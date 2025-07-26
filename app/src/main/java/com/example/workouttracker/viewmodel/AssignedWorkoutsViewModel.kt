package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.AssignedWorkoutModel
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.DatePickerDialogManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.ManageTeamsViewModel.ViewTeamAs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import com.example.workouttracker.R

/** View model to control the state of assigned workouts */
@HiltViewModel
class AssignedWorkoutsViewModel @Inject constructor(
    var teamRepository: TeamRepository,
    var userRepository: UserRepository,
    private var datePickerDialog: DatePickerDialogManager,
    private var resourceProvider: ResourceProvider
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
     * @param teamId the team id (0 if not used)
     */
    fun initializeData(teamId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.getAssignedWorkouts(
                startDate = Utils.formatDateToISO8601(_startDate.value),
                teamId = teamId,
                onSuccess = { _assignedWorkouts.value = it.toMutableList() },
                onFail = { _assignedWorkouts.value.clear() }
            )

            teamRepository.refreshMyTeams(teamType = ViewTeamAs.COACH.name)
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
                    _assignedWorkouts.value.clear()
                    _startDate.value = newDate
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