package com.example.workouttracker.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.UserModel
import com.example.workouttracker.data.network.repositories.UserProfileRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.ImagePickerEventBus
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

/** Edit profile view model to control the UI state change profile dialog */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private var resourceProvider: ResourceProvider,
    private var userProfileRepository: UserProfileRepository,
    private var userRepository: UserRepository,
    private val imagePickerBus: ImagePickerEventBus,
    private val vibrationManager: VibrationManager,
    private val dialogManager: DialogManager,
    private val snackbarManager: SnackbarManager
): ViewModel(), IImagePicker {

    /** Class containing all fields in the UI */
    data class UIState(
        val profileImage: String = "",
        val fullName: String = "",
        val fullNameError: String? = null
    )

    /** UI state of the dialog */
    private var _uiState = MutableStateFlow(UIState())
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
            imagePickerBus.requestImagePicker(this@EditProfileViewModel)
        }
    }

    /** Save the changes to the profile */
    fun save() {
        if (uiState.value.fullName.isEmpty()) {
            viewModelScope.launch { vibrationManager.makeVibration() }
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
                        dialogManager.hideDialog("EditProfileDialog")
                    }
                }
            )
        }
    }

    override fun onImageUploadSuccess(bitmap: Bitmap) {
        updateImage(Utils.convertBitmapToString(bitmap))
    }

    override fun onImageUploadFail() {
        viewModelScope.launch {
            snackbarManager.showSnackbar(R.string.error_msg_failed_to_upload_image)
        }
    }
}