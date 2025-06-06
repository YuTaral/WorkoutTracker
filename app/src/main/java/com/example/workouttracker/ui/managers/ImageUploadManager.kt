package com.example.workouttracker.ui.managers

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.workouttracker.utils.interfaces.IImagePicker

/** Image upload class to handle image uploading from the album or camera */
object ImageUploadManager {
    private lateinit var permissionResultHandler: PermissionResultHandler

    fun init (resultHandler: PermissionResultHandler) {
        permissionResultHandler = resultHandler
    }

    /**
     * Show dialog to select from where to upload the image
     * the image picker view model
     */
    suspend fun showImagePicker(imagePicker: IImagePicker) {
        permissionResultHandler.setImagePicker(imagePicker)

        AskQuestionDialogManager.askQuestion(
            DisplayAskQuestionDialogEvent(
                question = Question.IMAGE_SELECTION_OPTIONS,
                show = true,
                onCancel = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Use Photo Picker for Android 13+
                        openPhotoPicker()
                    } else {
                        // Handle older devices with permissions
                        openGallery()
                    }
                },
                onConfirm = {
                    openCamera()
                }
            ),
        )
    }

    /** Open the camera to allow image capture */
    private fun openCamera() {
        if (permissionResultHandler.checkPermissionGranted(Manifest.permission.CAMERA)) {
            // Permission granted, open the camera
            permissionResultHandler.cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            // Ask for the permission
            permissionResultHandler.cameraPermLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /** Open the gallery to allow image selection */
    private fun openGallery() {
        var permissionGranted = false
        val readMediaPermString = permissionResultHandler.getMediaPermissionString()

        if (permissionResultHandler.checkPermissionGranted(readMediaPermString)) {
            permissionGranted = true
        } else {
            permissionResultHandler.readMediaImagesPermLauncher.launch(readMediaPermString)
        }

        if (permissionGranted) {
            // Permission granted, open the gallery
            permissionResultHandler.galleryLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            )
        }
    }

    /** Open the photo picker */
    private fun openPhotoPicker() {
        permissionResultHandler.photoPickerLauncher
            .launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
