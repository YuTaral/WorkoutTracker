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
    private var resourceProvider: ResourceProvider,
    private var askQuestionManager: AskQuestionDialogManager,
    private val dialogManager: DialogManager
): ViewModel() {

    /** Logout user */
    fun logout() {
        viewModelScope.launch {
            askQuestionManager.askQuestion(
                DisplayAskQuestionDialogEvent(
                    question = Question.LOG_OUT,
                    show = true,
                    onConfirm = {
                        viewModelScope.launch(Dispatchers.IO) {
                            userRepository.logout()
                        }
                    }
                ),
            )
        }
    }

    /** Show change default values dialog */
    fun showChangeDefaultValues() {
        viewModelScope.launch {
            dialogManager.showDialog(
                title = resourceProvider.getString(R.string.exercise_default_values),
                dialogName = "ExerciseDefaultValuesDialog",
                content = { ExerciseDefaultValuesDialog(values = null, exerciseName = "") }
            )
        }
    }

    /** Show change password dialog */
    fun showChangePassword() {
        viewModelScope.launch {
            dialogManager.showDialog(
                title = resourceProvider.getString(R.string.change_password),
                dialogName = "ChangePasswordDialog",
                content = { ChangePasswordDialog() }
            )
        }
    }

    /** Show edit profile dialog */
    fun showEditProfile() {
        viewModelScope.launch {
            dialogManager.showDialog(
                title = resourceProvider.getString(R.string.edit_profile),
                dialogName = "EditProfileDialog",
                content = { EditProfileDialog() }
            )
        }
    }
}
