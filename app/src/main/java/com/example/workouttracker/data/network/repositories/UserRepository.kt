package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.models.UserModel
import com.example.workouttracker.data.network.APIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** UserRepository class, used to execute all requests related to User and store user related data */
class UserRepository @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {

    /** Logged in user */
    private var _user = MutableStateFlow<UserModel?>(null)
    var user = _user.asStateFlow()

    /** Update the logged in user with the provided value */
    fun updateUser(value: UserModel?) {
        _user.value = value
        sharedPrefsManager.updateUserInPrefs(_user.value)
    }

    /**
     * Login the user with the given email and password
     * @param email the email
     * @param password the password
     */
    suspend fun login(email: String, password: String) {
        networkManager.sendRequest(
            request = { apiService.getInstance().login(mapOf("email" to email, "password" to password)) },
            onSuccessCallback = { response ->
                updateUser(UserModel(response.data[0]))
                apiService.updateToken(response.data[1])
            }
        )
    }

    /** Register the user with the given email and password
     * @param email the email
     * @param password the password
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun register(email: String, password: String, onSuccess: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().register(mapOf("email" to email, "password" to password)) },
            onSuccessCallback = { onSuccess() })
    }

    /** Logout the user
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun logout(onSuccess: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().logout() },
            onSuccessCallback = {
                onSuccess()
            })
    }

    /** Change user password
     * @param oldPassword the old password
     * @param password the new password
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun changePassword(oldPassword: String, password: String, onSuccess: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().changePassword(mapOf("oldPassword" to oldPassword, "password" to password)) },
            onSuccessCallback = { onSuccess() })
    }

    /** Validate the token
     * @param token the token
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun validateToken(token: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().validateToken(mapOf("token" to token)) },
            onSuccessCallback = { onSuccess() },
            onErrorCallback =  { onFailure() }
        )
    }
}