package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.models.UserDefaultValuesModel
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

    /** Update the JWT bearer token */
    private fun updateToken(token: String) {
        apiService.updateToken(token)
        sharedPrefsManager.updateTokenInPrefs(token)
    }

    /** Update the user default values with the provided value */
    fun updateDefaultValues(value: UserDefaultValuesModel) {
        // Simulate creation of new object to trigger recompositions
        // and update the weight unit labels
        val currentUser = _user.value!!
        val newUser = UserModel(
            idVal = currentUser.id,
            emailVal = currentUser.email,
            fullNameVal = currentUser.fullName,
            profileImageVal = currentUser.profileImage,
            defaultValuesVal = value
        )

        updateUser(newUser)
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
                updateToken(response.data[1])
                updateUser(UserModel(response.data[0]))
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
                updateUser(null)
                updateToken("")
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