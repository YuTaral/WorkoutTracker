package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.ui.managers.ShowHideDialogEvent
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
        viewModelScope.launch {
            AskQuestionDialogManager.askQuestion(
                ShowHideDialogEvent(
                    question = Question.LOG_OUT,
                    show = true,
                    onCancel = {
                        viewModelScope.launch {
                            AskQuestionDialogManager.hideQuestion()
                        }
                    },
                    onConfirm = {
                        viewModelScope.launch(Dispatchers.IO) {
                            userRepository.logout(onSuccess = {
                                viewModelScope.launch {
                                    AskQuestionDialogManager.hideQuestion()
                                }
                            })
                        }
                    }
                ),
            )
        }
    }
}
