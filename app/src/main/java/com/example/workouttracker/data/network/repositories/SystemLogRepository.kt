package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.network.APIService
import javax.inject.Inject

/** System log repository to store system log when exception occurs */
class SystemLogRepository @Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {
    /** Add system log
     * @param message the message
     * @param stackTrace the stack trace
     */
    suspend fun addSystemLog(message: String, stackTrace: String) {
        val params = mapOf("message" to message, "stackTrace" to stackTrace)

        networkManager.sendRequest(
            request = { apiService.getInstance().addSystemLog(params) },
            onSuccessCallback = { },
            blockUi = false
        )
    }
}