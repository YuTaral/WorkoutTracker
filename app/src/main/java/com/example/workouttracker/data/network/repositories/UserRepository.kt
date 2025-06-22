package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.models.UserDefaultValuesModel
import com.example.workouttracker.data.models.UserModel
import com.example.workouttracker.data.network.APIService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    /** Track event to ask user for all permissions on first app login */
    private val _requestPermissions = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val requestPermissions = _requestPermissions.asSharedFlow()

    /** Update the logged in user with the provided value */
    fun updateUser(value: UserModel?) {
        _user.value = value
        sharedPrefsManager.updateUserInPrefs(_user.value)
    }

    /** Emit event to ask user for all the necessary permissions */
    suspend fun requestAllPerm() {
        _requestPermissions.emit(Unit)
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
     * @param onSuccess callback to execute on successful login
     */
    suspend fun login(email: String, password: String, onSuccess: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().login(mapOf("email" to email, "password" to password)) },
            onSuccessCallback = { response ->
                updateToken(response.data[1])
                updateUser(UserModel(response.data[0]))
                onSuccess()
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

    /** Logout the user */
    suspend fun logout() {
        networkManager.sendRequest(
            request = { apiService.getInstance().logout() },
            onSuccessCallback = {
                updateUser(null)
                updateToken("")
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