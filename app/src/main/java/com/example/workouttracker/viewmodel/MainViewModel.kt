package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.network.repositories.NotificationRepository
import com.example.workouttracker.data.network.repositories.UserRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** MainViewModel to manage the state of the main screen */
@HiltViewModel
class MainViewModel @Inject constructor(
    var userRepository: UserRepository,
    var notificationRepository: NotificationRepository,
    private var sharedPrefsManager: SharedPrefsManager
): ViewModel() {
    private val _tokenValidated = MutableStateFlow(false)
    val tokenValidated = _tokenValidated.asStateFlow()

    init {
        checkAutoLogin()
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
}