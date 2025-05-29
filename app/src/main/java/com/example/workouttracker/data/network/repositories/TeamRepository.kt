package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.models.TeamMemberModel
import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.models.NotificationModel
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.data.models.TeamWithMembersModel
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** TeamRepository class, used to execute all requests related to teams */
class TeamRepository @Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {

    /** The user teams */
    private var _teams = MutableStateFlow<MutableList<TeamModel>>(mutableListOf())
    var teams = _teams.asStateFlow()

    /** The currently selected team */
    private var _selectedTeam = MutableStateFlow<TeamModel?>(null)
    var selectedTeam = _selectedTeam.asStateFlow()

    /** The selected team members */
    private val _teamMembers = MutableStateFlow<List<TeamMemberModel>>(listOf())
    val teamMembers = _teamMembers.asStateFlow()

    /** Add new team
     * @param team the team data
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun addTeam(team: TeamModel, onSuccess: () -> Unit) {
        // Send a request to add the team
        val params = mapOf("team" to Utils.serializeObject(team))

        networkManager.sendRequest(
            request = { apiService.getInstance().addTeam(params) },
            onSuccessCallback = { onSuccess() }
        )
    }

    /** Update team
     * @param team the team data
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun editTeam(team: TeamModel, onSuccess: () -> Unit) {
        val params = mapOf("team" to Utils.serializeObject(team))

        networkManager.sendRequest(
            request = { apiService.getInstance().updateTeam(params) },
            onSuccessCallback = { onSuccess() },
        )
    }

    /** Delete the team
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun deleteTeam(teamId: Long, onSuccess: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().deleteTeam(teamId) },
            onSuccessCallback = { onSuccess() }
        )
    }

    /** Leave the team
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun leaveTeam(teamId: Long, onSuccess: () -> Unit) {
        val params = mapOf("teamId" to teamId.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().leaveTeam(params) },
            onSuccessCallback = { onSuccess() }
        )
    }

    /** Invite member to team
     * @param userId the user id to invite
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun inviteMember(userId: String, teamId: Long, onSuccess: (List<TeamMemberModel>) -> Unit) {
        val params = mapOf("userId" to userId, "teamId" to teamId.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().inviteMember(params) },
            onSuccessCallback = { response ->
                val members: MutableList<TeamMemberModel> = mutableListOf()

                if (response.data.isNotEmpty()) {
                    members.addAll(response.data.map { TeamMemberModel(it) })
                }

                onSuccess(members)
            },
        )
    }

    /** Accept invite
     * @param userId the user id who accepted the invite
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun acceptInvite(userId: String, teamId: Long, onSuccess: () -> Unit) {
        val params = mapOf("userId" to userId, "teamId" to teamId.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().acceptInvite(params) },
            onSuccessCallback = { onSuccess()},
        )
    }

    /** Decline invite
     * @param userId the user id who accepted the invite
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun declineInvite(userId: String, teamId: Long, onSuccess: (List<NotificationModel>) -> Unit) {
        val params = mapOf("userId" to userId, "teamId" to teamId.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().declineInvite(params) },
            onSuccessCallback = { response -> onSuccess(response.data.map {NotificationModel(it)})},
        )
    }

    /** Remove member from team
     * @param member the team member to remove
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun removeMember(member: TeamMemberModel, onSuccess: (List<TeamMemberModel>) -> Unit) {
        val params = mapOf("member" to Utils.serializeObject(member))

        networkManager.sendRequest(
            request = { apiService.getInstance().removeMember(params) },
            onSuccessCallback = { response ->
                val members: MutableList<TeamMemberModel> = mutableListOf()

                if (response.data.isNotEmpty()) {
                    members.addAll(response.data.map { TeamMemberModel(it) })
                }

                onSuccess(members)
            },
        )
    }

    /** Get the teams created by the user
     * @param teamType the team type - as coach or as member
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun refreshMyTeams(teamType: String) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getMyTeams(teamType) },
            onSuccessCallback = { response ->
                _teams.value = response.data.map { TeamModel(it) } as MutableList<TeamModel>
            },
        )
    }

    /** Get the teams created by the user, which have 1 or more members
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun getMyTeamsWithMembers(onSuccess: (List<TeamWithMembersModel>) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getMyTeamsWithMembers() },
            onSuccessCallback = { response -> onSuccess(response.data.map { TeamWithMembersModel(it) } )},
        )
    }

    /** Perform search for users with the given name which are valid for team invitation
     * @param name the name to search
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun getUsersToInvite(name: String, teamId: Long, onSuccess: (List<TeamMemberModel>) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getUsersToInvite(name, teamId) },
            onSuccessCallback = { response ->
                val members:MutableList<TeamMemberModel> = mutableListOf()

                if (response.data.isNotEmpty()) {
                    members.addAll(response.data.map { TeamMemberModel(it) })
                }

                onSuccess(members)
            },
        )
    }

    /** Get team members when logged in user is coach
     * @param teamId the team id
     */
    suspend fun refreshMyTeamMembers(teamId: Long) {
        if (teamId == 0L) {
            _teamMembers.value = mutableListOf()
            return
        }

        networkManager.sendRequest(
            request = { apiService.getInstance().getTeamMembers(teamId) },
            onSuccessCallback = { response ->
                _teamMembers.value = response.data.map { TeamMemberModel(it) }
            },
        )
    }

    /** Update my team members with the provided value */
    fun updateMyTeamMembers(data: List<TeamMemberModel>) {
        _teamMembers.value = data
    }

    /** Get team members when logged in user is member
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun getJoinedTeamMembers(teamId: Long, onSuccess: (List<String>) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getJoinedTeamMembers(teamId) },
            onSuccessCallback = { response ->
                onSuccess(response.data)
            },
        )
    }

    /**
     * Update the selected team and members
     * @param team selected team, may be null
     */
    suspend fun updateSelectedTeam(team: TeamModel?) {
        _selectedTeam.value = team

        if (_selectedTeam.value != null) {
            refreshMyTeamMembers(teamId = _selectedTeam.value!!.id)
        } else {
            _teamMembers.value = mutableListOf()
        }
    }
}