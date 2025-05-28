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
import com.example.workouttracker.utils.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Enum with team types when fetching teams */
enum class ViewTeamAs(private val stringId: Int) {
    COACH(R.string.as_coach_lbl),
    MEMBER(R.string.as_member_lbl);

    fun getStringId(): Int {
        return stringId
    }
}

@HiltViewModel
class ManageTeamsViewModel @Inject constructor(
    private var resourceProvider: ResourceProvider,
    private var teamRepository: TeamRepository
): ViewModel() {

    /** The teams the user owns / participates as member */
    private var _teams = MutableStateFlow<List<TeamModel>>(listOf())
    var teams = _teams.asStateFlow()

    /** Track the selected team type */
    private var _selectedTeamType = MutableStateFlow<ViewTeamAs>(ViewTeamAs.COACH)
    var selectedTeamType = _selectedTeamType.asStateFlow()

    /** Track the selected team */
    private var _selectedTeam: TeamModel? = null

    /** Initialize the data when the screen is created */
    fun initializeData() {
        updateSelectedTeamType(resourceProvider.getString(ViewTeamAs.COACH.getStringId()))
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
        _selectedTeam = value

        viewModelScope.launch {
            if (_selectedTeam == null) {
                PagerManager.changePageSelection(Page.ManageTeams)
            } else {
                PagerManager.changePageSelection(Page.EditTeam(team = value!!))
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
            PagerManager.changePageSelection(Page.AddTeam)
        }
    }

    /** Refresh teams after team type selection */
    private fun refreshTeams() {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.getMyTeams(
                teamType = _selectedTeamType.value.name,
                onSuccess = { _teams.value = it }
            )
        }
    }
}