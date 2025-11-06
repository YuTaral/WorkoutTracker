package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.ui.managers.DatePickerDialogManager
import com.example.workouttracker.utils.Constants.ViewTeamAs
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/** View model to control the UI state of assigned workouts filters dialog */
@HiltViewModel
class AssignedWorkoutsFiltersViewModel @Inject constructor(
    var teamRepository: TeamRepository,
    private var resourceProvider: ResourceProvider,
    private var datePickerDialog: DatePickerDialogManager
) : ViewModel() {

    /** Selected assigned workouts start date */
    private var _startDate = MutableStateFlow(getDefaultStartDate())
    var startDate = _startDate.asStateFlow()

    /** Selected team filter if any */
    private var _teamFilter =  MutableStateFlow(getDefaultTeamFilter())
    var teamFilter = _teamFilter.asStateFlow()

    /** Track the selected team type */
    private var _selectedTeamType = MutableStateFlow<ViewTeamAs>(ViewTeamAs.COACH)
    var selectedTeamType = _selectedTeamType.asStateFlow()

    /**
     * Initialize the data required to populate the dialog
     * @param startDateValue the start date filter value
     * @param teamFilterValue the team filter value
     * @param selectedTeamTypeValue the selected team type value
     */
    fun initializeData(startDateValue: Date, teamFilterValue: TeamModel, selectedTeamTypeValue: ViewTeamAs) {
        _startDate.value = startDateValue
        _teamFilter.value = teamFilterValue
        _selectedTeamType.value = selectedTeamTypeValue
    }

    /** Update the selected team type to the provided value */
    fun updateSelectedTeamType(value: String) {
        val matchingEnum = ViewTeamAs.entries.firstOrNull { enum ->
            resourceProvider.getString(enum.getStringId()) == value
        }

        if (matchingEnum != null) {
            _selectedTeamType.value = matchingEnum

            viewModelScope.launch(Dispatchers.IO) {
                teamRepository.refreshMyTeams(teamType = _selectedTeamType.value.name)
                teamRepository.teams.value.add(0, getDefaultTeamFilter())
            }
        }
    }

    /**
     * Update the selected team filter
     * @param teamId the team id to filter by
     */
    fun updateTeamFilter(teamId: String) {
        _teamFilter.value = teamRepository.teams.value.firstOrNull { it.id.toString() == teamId } ?: getDefaultTeamFilter()
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

                    _startDate.value = newDate
                }
            )
        }
    }

    /** Return the default team filter */
    private fun getDefaultTeamFilter(): TeamModel {
        return TeamModel(0L, "", resourceProvider.getString(R.string.all_teams_lbl), "")
    }

    /** Return the default assigned workouts start date - 1 week backwards */
    private fun getDefaultStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -7)
        return calendar.time
    }
}