package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.workouttracker.R
import com.example.workouttracker.ui.dialogs.ChangePasswordDialog
import com.example.workouttracker.ui.dialogs.EditProfileDialog
import com.example.workouttracker.ui.dialogs.ExerciseDefaultValuesDialog

/** Drawer view model to manage the state of the Drawer */
@HiltViewModel
class DrawerViewModel @Inject constructor(
    private var userRepository: UserRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** Logout user */
    fun logout() {
        viewModelScope.launch {
            AskQuestionDialogManager.askQuestion(
                DisplayAskQuestionDialogEvent(
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

    /** Show change default values dialog */
    fun showChangeDefaultValues() {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.exercise_default_values),
                dialogName = "ExerciseDefaultValuesDialog",
                content = { ExerciseDefaultValuesDialog(values = null, exerciseName = "") }
            )
        }
    }

    /** Show change password dialog */
    fun showChangePassword() {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.change_password),
                dialogName = "ChangePasswordDialog",
                content = { ChangePasswordDialog() }
            )
        }
    }

    /** Show edit profile dialog */
    fun showEditProfile() {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.edit_profile),
                dialogName = "EditProfileDialog",
                content = { EditProfileDialog() }
            )
        }
    }
}
