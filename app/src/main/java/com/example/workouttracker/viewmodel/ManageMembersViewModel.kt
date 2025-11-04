package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.TeamMemberModel
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.ui.components.MemberTeamState
import com.example.workouttracker.utils.Constants.ViewTeamAs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** View model to control the UI state of manage team members dialog */
@HiltViewModel
class ManageMembersViewModel @Inject constructor(
    var teamRepository: TeamRepository
): ViewModel() {

    /** The search results */
    private var _searchResult = MutableStateFlow<MutableList<TeamMemberModel>>(mutableListOf())
    var searchResult = _searchResult.asStateFlow()

    /** Whether to show message for no users found. Set to true after search */
    private var _showNoUsersFound = MutableStateFlow<Boolean>(false)
    var showNoUsersFound = _showNoUsersFound.asStateFlow()

    /** The search term for members */
    private var _search = MutableStateFlow<String>("")
    var search = _search.asStateFlow()

    /**
     * Initialize the data when the dialog is shown
     */
    fun initializeData() {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.refreshMyTeamMembers(teamId = teamRepository.selectedTeam.value!!.id)
        }
        _searchResult.value = mutableListOf()
        _showNoUsersFound.value = false
        updateSearch("")
    }

    /** Update the selected and all teams when the dialog is being closed */
    fun updateTeamsOnClose() {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.refreshMyTeams(teamType = ViewTeamAs.COACH.name)
            teamRepository.updateSelectedTeam(
                team = teamRepository.teams.value.find { it.id == teamRepository.selectedTeam.value!!.id }
            )
        }
    }

    /** Update the search with the provided value */
    fun updateSearch(value: String) {
        _search.value = value
    }

    /**
     * Execute callback when team member button is clicked
     * @param member the team member
     */
    fun onClick(member: TeamMemberModel) {
        val state = MemberTeamState.valueOf(member.teamState)

        if (state == MemberTeamState.NOT_INVITED) {
            inviteMember(member)
        } else {
            removeMember(member)
        }
    }

    /** Search the for members */
    fun searchMembers() {
        if (_search.value.isEmpty()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.getUsersToInvite(
                name = _search.value,
                teamId = teamRepository.selectedTeam.value!!.id,
                onSuccess = {
                    _searchResult.value = it as MutableList
                    _showNoUsersFound.value = _searchResult.value.isEmpty()
                }
            )
        }
    }

    /**
     * Invite the member to the team
     * @param member the member
     */
    private fun inviteMember(member: TeamMemberModel) {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.inviteMember(
                userId = member.userId,
                teamId = member.teamId,
                onSuccess = {
                    teamRepository.updateMyTeamMembers(it)
                    _searchResult.value = _searchResult.value.toMutableList().apply {
                        remove(member)
                    }
                }
            )
        }
    }

    /**
     * Remove the member from the team
     * @param member the member
     */
    private fun removeMember(member: TeamMemberModel) {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.removeMember(
                member = member,
                onSuccess = { teamRepository.updateMyTeamMembers(it) }
            )
        }
    }
}