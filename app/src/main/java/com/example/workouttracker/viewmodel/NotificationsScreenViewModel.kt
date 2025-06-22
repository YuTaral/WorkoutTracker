package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.NotificationModel
import com.example.workouttracker.data.network.repositories.NotificationRepository
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.utils.Constants.NotificationType
import com.example.workouttracker.viewmodel.ManageTeamsViewModel.ViewTeamAs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/** View model to control the state of notifications screen */
@HiltViewModel
class NotificationsScreenViewModel @Inject constructor(
    var notificationRepository: NotificationRepository,
    private var teamRepository: TeamRepository,
    private var userRepository: UserRepository,
    private var askQuestionManager: AskQuestionDialogManager
): ViewModel() {

    /** Initialize the data when the screen is displayed */
    fun initializeData() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.refreshNotifications()
        }
    }

    /**
     * Callback to execute on notification click
     * @param notification the notification
     */
    fun onClick(notification: NotificationModel) {
        if (notification.type == NotificationType.INVITED_TO_TEAM.toString()) {
            askJoinTeam(notification = notification)

        } else if (notification.type == NotificationType.JOINED_TEAM.toString() ||
            notification.type == NotificationType.DECLINED_TEAM_INVITATION.toString()) {

            if (notification.isActive) {
                viewModelScope.launch(Dispatchers.IO) {
                    notificationRepository.reviewNotification(id = notification.id)
                }
            }

            redirectToTeam(
                teamId = notification.teamId!!,
                teamType = ViewTeamAs.COACH
            )
        }
    }

    /**
     * Callback to execute on notification remove click
     * @param id the notification id to remove
     */
    fun removeNotification(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.deleteNotification(notificationId = id)
        }
    }

    /**
     * Ask user to accept / decline team invitation
     * @param notification the notification data
     */
    private fun askJoinTeam(notification: NotificationModel) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.getNotificationDetails(
                notificationId = notification.id,
                onSuccess = { notificationDetails ->
                    viewModelScope.launch {
                        askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                            question = Question.JOIN_TEAM,
                            show = true,
                            onCancel = { declineInvite(notification.teamId!!) },
                            onConfirm = { acceptInvite(notification.teamId!!) },
                            formatQValues = listOf(notificationDetails.description),
                            formatTitle = notificationDetails.teamName
                        ))
                    }
                }
            )
        }
    }

    /**
     * Decline team invitation
     * @param teamId the team id
     */
    private fun declineInvite(teamId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.declineInvite(
                userId = userRepository.user.value!!.id,
                teamId = teamId,
                onSuccess = {
                    viewModelScope.launch(Dispatchers.IO) {
                        notificationRepository.refreshNotifications()
                    }
                }
            )
        }
    }

    /**
     * Accept team invitation
     * @param teamId the team id
     */
    fun acceptInvite(teamId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.acceptInvite(
                userId = userRepository.user.value!!.id,
                teamId = teamId,
                onSuccess = {
                    viewModelScope.launch {
                        redirectToTeam(
                            teamId = teamId,
                            teamType = ViewTeamAs.MEMBER
                        )
                    }
                }
            )
        }
    }

    /** Redirect to team
     * @param teamId the team id to mark as selected and redirect to
     */
    private fun redirectToTeam(teamId: Long, teamType: ViewTeamAs) {
        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.refreshMyTeams(
                teamType = teamType.name,
                callback = {
                    viewModelScope.launch {
                        teamRepository.updateSelectedTeam(
                            team = teamRepository.teams.value.find { it.id == teamId },
                            callback = {
                                viewModelScope.launch {
                                    PagerManager.changePageSelection(
                                        Page.EditTeam(team = teamRepository.selectedTeam.value!!)
                                    )
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}