package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.models.JoinTeamNotificationModel
import com.example.workouttracker.data.models.NotificationModel
import com.example.workouttracker.data.network.APIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** NotificationRepository class, used to execute all requests related to notifications */
class NotificationRepository @Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {

    /** Track whether the user has new notifications */
    private var _notification = MutableStateFlow<Boolean>(false)
    var notification = _notification.asStateFlow()

    /** List of notifications */
    private var _notifications = MutableStateFlow<List<NotificationModel>>(listOf())
    var notifications = _notifications.asStateFlow()

    /** Refresh the notifications for the logged in user */
    suspend fun refreshNotifications() {
        networkManager.sendRequest(
            request = { apiService.getInstance().getNotifications() },
            onSuccessCallback = { response -> _notifications.value = response.data.map { NotificationModel(it) }},
        )
    }

    /** Get the notification details
     * @param notificationId the notification id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun getNotificationDetails(notificationId: Long, onSuccess: (JoinTeamNotificationModel) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getJoinTeamNotificationDetails(notificationId) },
            onSuccessCallback = { response -> onSuccess(JoinTeamNotificationModel(response.data[0]))},
        )
    }

    /** Mark the notification as reviewed (inactive)
     * @param id the notification id
     */
    suspend fun reviewNotification(id: Long) {
        val params = mapOf("id" to id.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().notificationReviewed(params) },
            onSuccessCallback = {},
        )
    }

    /** Delete the notification and refresh the data on success
     * @param notificationId the notification id to remove
     */
    suspend fun deleteNotification(notificationId: Long) {
        networkManager.sendRequest(
            request = { apiService.getInstance().deleteNotification(notificationId) },
            onSuccessCallback = { response -> _notifications.value = response.data.map { NotificationModel(it) }},
        )
    }

    /** Send refresh request to update notifications
     * @param onResponse callback to execute when response is received
     */
    suspend fun refreshNotifications(onResponse: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().refreshNotifications() },
            onSuccessCallback = { onResponse() },
        )
    }

    /** Update the notification with the provided value */
    fun updateNotification(value: Boolean) {
        _notification.value = value
    }
}