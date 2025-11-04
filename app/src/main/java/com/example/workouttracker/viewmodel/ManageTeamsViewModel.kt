package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.utils.Constants.ViewTeamAs
import com.example.workouttracker.utils.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class ManageTeamsViewModel @Inject constructor(
    private var resourceProvider: ResourceProvider,
    private val pagerManager: PagerManager,
    var teamRepository: TeamRepository,
): ViewModel() {

    /** Track the selected team type */
    private var _selectedTeamType = MutableStateFlow<ViewTeamAs>(ViewTeamAs.COACH)
    var selectedTeamType = _selectedTeamType.asStateFlow()

    /**
     * Initialize the data when the screen is created
     * @param teamType selected team type
     */
    fun initializeData(teamType: ViewTeamAs) {
        updateSelectedTeamType(resourceProvider.getString(teamType.getStringId()))
        updateSelectedTeam(null)
    }

    /** Update the selected team type to the provided value */
    fun updateSelectedTeamType(value: String) {
        val matchingEnum = ViewTeamAs.entries.firstOrNull { enum ->
            resourceProvider.getString(enum.getStringId()) == value
        }

        if (matchingEnum != null) {
            _selectedTeamType.value = matchingEnum
            refreshTeams()
        }
    }

    /** Update the selected team with the provided value */
    fun updateSelectedTeam(value: TeamModel?) {
        viewModelScope.launch {
            teamRepository.updateSelectedTeam(value)

            if (teamRepository.selectedTeam.value == null) {
                pagerManager.changePageSelection(Page.ManageTeams(teamType = ViewTeamAs.COACH))
            } else {
                pagerManager.changePageSelection(Page.EditTeam(team = value!!))
            }
        }
    }

    /** Return the string id of the message to display when there are no teams */
    fun getNoTeamsMessage(): Int {
        return if (_selectedTeamType.value == ViewTeamAs.COACH) {
            R.string.no_my_teams_lbl
        } else {
            R.string.no_joined_teams_lbl
        }
    }

    /** Show the panel to add new team */
    fun showAddTeam() {
        viewModelScope.launch {
            pagerManager.changePageSelection(Page.AddTeam)
        }
    }

    /** Refresh teams after team type selection */
    private fun refreshTeams() {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.refreshMyTeams(teamType = _selectedTeamType.value.name)
        }
    }
}