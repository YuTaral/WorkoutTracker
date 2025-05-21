package com.example.workouttracker.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.UserModel
import com.example.workouttracker.data.network.repositories.UserProfileRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.ImageUploadManager
import com.example.workouttracker.ui.managers.SnackbarManager
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.utils.interfaces.IImagePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val profileImage: String = "",
    val fullName: String = "",
    val fullNameError: String? = null
)

/** Edit profile view model to control the UI state change profile dialog */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private var resourceProvider: ResourceProvider,
    private var userProfileRepository: UserProfileRepository,
    private var userRepository: UserRepository
): ViewModel(), IImagePicker {

    /** UI state of the dialog */
    private var _uiState = MutableStateFlow(EditProfileUiState())
    var uiState = _uiState.asStateFlow()

    /** Initialize the state when the dialog is opened */
    fun initializeState() {
        var user = userRepository.user.value!!

        updateImage(user.profileImage)
        updateName(user.fullName)
    }

    init {
        initializeState()
    }

    /** Update the image in the UI with the provided value */
    fun updateImage(value: String) {
        _uiState.update { it.copy(profileImage = value) }
    }

    /** Update the name in the UI with the provided value */
    fun updateName(value: String) {
        _uiState.update { it.copy(fullName = value) }
    }

    /** Update the name error in the UI with the provided value */
    fun updateNameError(value: String?) {
        _uiState.update { it.copy(fullNameError = value) }
    }

    /** Change the profile image on click */
    fun onImageClick() {
        viewModelScope.launch {
            ImageUploadManager.showImagePicker(imagePicker = this@EditProfileViewModel)
        }
    }

    /** Save the changes to the profile */
    fun save() {
        if (uiState.value.fullName.isEmpty()) {
            viewModelScope.launch { VibrationManager.makeVibration() }
            updateNameError(resourceProvider.getString(R.string.error_msg_username_cannot_be_blank))
            return
        } else {
            updateNameError(null)
        }

        val newUser = UserModel(
            idVal = userRepository.user.value!!.id,
            emailVal = userRepository.user.value!!.email,
            fullNameVal = uiState.value.fullName,
            profileImageVal = uiState.value.profileImage,
            defaultValuesVal = userRepository.user.value!!.defaultValues
        )

        viewModelScope.launch(Dispatchers.IO) {
            userProfileRepository.updateUserProfile(
                user = newUser,
                onSuccess = {
                    userRepository.updateUser(it)
                    viewModelScope.launch {
                        DialogManager.hideDialog("EditProfileDialog")
                    }
                }
            )
        }
    }

    override fun onImageUploadSuccess(bitmap: Bitmap) {
        viewModelScope.launch {
            AskQuestionDialogManager.hideQuestion()
        }
        updateImage(Utils.convertBitmapToString(bitmap))
    }

    override fun onImageUploadFail() {
        viewModelScope.launch {
            AskQuestionDialogManager.hideQuestion()
            SnackbarManager.showSnackbar(R.string.error_msg_failed_to_upload_image)
        }
    }
}