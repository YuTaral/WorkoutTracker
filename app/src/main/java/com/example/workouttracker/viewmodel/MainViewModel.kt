package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.network.repositories.NotificationRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.VibrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** MainViewModel to manage the state of the main screen */
@HiltViewModel
class MainViewModel @Inject constructor(
    var userRepository: UserRepository,
    var notificationRepository: NotificationRepository,
    var sharedPrefsManager: SharedPrefsManager,
    var vibrationManager: VibrationManager
): ViewModel() {

    /** Track when the token has been validated */
    private val _tokenValidated = MutableStateFlow(false)
    val tokenValidated = _tokenValidated.asStateFlow()

    /** Job to refresh the notification on every N seconds */
    private var refreshJob: Job? = null

    init {
        checkAutoLogin()
        scheduleRefreshNotification()
    }

    /** Performs a check whether there is stored user and valid token and if so auto login the user */
    private fun checkAutoLogin() {
        val token = sharedPrefsManager.getStoredToken()
        val userModel = sharedPrefsManager.getStoredUser()

        if (userModel != null && token.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.validateToken(
                    token = token,
                    onSuccess = {
                        userRepository.updateUser(userModel)
                        _tokenValidated.value = true
                    },
                    onFailure = {
                        _tokenValidated.value = true
                    }
                )
            }
        } else {
            _tokenValidated.value = true
        }
    }

    /** Schedule background job to refresh the notification on every 30 seconds */
    fun scheduleRefreshNotification() {
        refreshJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                notificationRepository.refreshNotification()
                delay(30000L)
            }
        }
    }
}