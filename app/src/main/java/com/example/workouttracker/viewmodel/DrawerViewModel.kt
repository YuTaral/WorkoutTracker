package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.network.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Drawer view model to manage the state of the Drawer */
@HiltViewModel
class DrawerViewModel @Inject constructor(
    private var userRepository: UserRepository

): ViewModel() {

    /** Logout user */
    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.logout(onSuccess = {
                userRepository.updateUser(null)
            })
        }
    }
}