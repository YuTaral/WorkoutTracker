package com.example.workouttracker.ui.managers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.workouttracker.R
import com.example.workouttracker.ui.MainActivity
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.utils.interfaces.IImagePicker
import kotlinx.coroutines.launch

/** Class to handle the logic when requesting permissions / launching specific result launcher */
class PermissionResultHandler(mainActivity: MainActivity) {
    private var activity = mainActivity
    private var imagePicker: IImagePicker? = null
    var notificationPermLauncher: ActivityResultLauncher<String>
    var cameraPermLauncher: ActivityResultLauncher<String>
    var readMediaImagesPermLauncher: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    lateinit var photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    init {
        // Initialize the permission launchers
        cameraPermLauncher = initializeRequestPermissionLaunchers(Manifest.permission.CAMERA)
        readMediaImagesPermLauncher = initializeRequestPermissionLaunchers(getMediaPermissionString())
        notificationPermLauncher = initializeRequestPermissionLaunchers(getNotificationsPermString())

        initializeActivityResultLaunchers()
    }

    /** Set the image picker to execute the callback when image pick is successful */
    fun setImagePicker(picker: IImagePicker?) {
        imagePicker = picker
    }

    /** Initialize the launchers for requesting permissions */
    private fun initializeRequestPermissionLaunchers(permission: String): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onResultInActivity(isGranted, permission)
        }
    }

    /** Execute the logic to ask for all permission one after another when the asking for permissions
     * in login activity (app first start)
     * @param permission the permission
     */
    private fun onResultInActivity(permission: String) {
        when (permission) {
            (Manifest.permission.CAMERA) -> {
                // Ask for next permission
                readMediaImagesPermLauncher.launch(getMediaPermissionString())
            }
            (getMediaPermissionString()) -> {
                notificationPermLauncher.launch(getNotificationsPermString())
            }
        }
    }

    /** Execute the logic when result for granting permission is returned
     * @param isGranted true if the permission was granted, false otherwise
     * @param permission the permission
     */
    private fun onResultInActivity(isGranted: Boolean, permission: String) {
        if (isGranted) {
            when (permission) {
                // Execute the action based on the current requested permission
                (Manifest.permission.CAMERA) -> {
                    cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                }

                (getMediaPermissionString()) -> {
                    galleryLauncher.launch(
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                    )
                }

            }
        } else {
            if (permission != Manifest.permission.CAMERA) {
                return
            }

            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // Permission set to "Don't ask again" or permanently denied, open the settings
                showSettingsDialogForCamera()
            } else {
                // Permission denied
                activity.lifecycleScope.launch {
                    SnackbarManager.showSnackbar(R.string.permission_denied_message)
                }
            }
        }
    }

    /** Initialize the launchers for open camera / photos */
    private fun initializeActivityResultLaunchers() {
        // Initialize camera launcher
        cameraLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.extras != null) {

                val capturedImageBitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // API 33+ (Android 13 and above)
                    result.data?.extras?.getParcelable("data", Bitmap::class.java)
                } else {
                    // For older versions below API 33
                    @Suppress("DEPRECATION")
                    result.data?.extras?.getParcelable("data")
                }

                if (capturedImageBitmap == null) {
                    return@registerForActivityResult
                }

                onLauncherResultOk(capturedImageBitmap)

            }
        }

        // Initialize gallery launcher
        galleryLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                onLauncherResultOk(result.data?.data!!)
            }
        }

        // Initialize photo picker launcher
        photoPickerLauncher = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                onLauncherResultOk(uri)
            }
        }
    }

    /** Execute the callback when launcher result is OK
     * @param bitmap the bitmap after image successful image capture
     */
    private fun onLauncherResultOk(bitmap: Bitmap) {
        val scaledBitmap = Utils.scaleBitmap(bitmap)

        if (imagePicker == null) {
            return
        }

        if (scaledBitmap != null) {
            imagePicker!!.onImageUploadSuccess(scaledBitmap)
        } else {
            imagePicker!!.onImageUploadFail()
        }

        imagePicker = null
    }

    /** Execute the callback when launcher result is OK
     * @param uri the image uri
     */
    private fun onLauncherResultOk(uri: Uri) {
        val bitmap =  Utils.scaleBitmap(uri)

        if (imagePicker == null) {
            return
        }

        if (bitmap != null) {
            imagePicker!!.onImageUploadSuccess(bitmap)
        } else {
            imagePicker!!.onImageUploadFail()
        }

        imagePicker = null
    }

    /** Ask the user to open settings and change the permission */
    private fun showSettingsDialogForCamera() {
        activity.lifecycleScope.launch {
            AskQuestionDialogManager.askQuestion(
                DisplayAskQuestionDialogEvent(
                    question = Question.ALLOW_CAMERA_PERMISSION,
                    show = true,
                    onCancel = {
                        activity.lifecycleScope.launch {
                            AskQuestionDialogManager.hideQuestion()
                        }
                    },
                    onConfirm = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", activity.packageName, null)
                        )

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        activity.startActivity(intent)

                        activity.lifecycleScope.launch {
                            AskQuestionDialogManager.hideQuestion()
                        }
                    }
                ),
            )
        }
    }

    /** Return the read media permission string based on the build version */
    fun getMediaPermissionString(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    /** Return the notifications permission string based on the build version */
    fun getNotificationsPermString(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        }
    }

    /** Return true the permission is granted, false otherwise
     * @param permission the permission
     */
    fun checkPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
}