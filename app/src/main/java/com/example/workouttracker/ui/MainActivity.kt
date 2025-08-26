package com.example.workouttracker.ui

import android.animation.ObjectAnimator
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.example.workouttracker.ui.screens.Screen
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.workouttracker.ui.managers.ImagePickerEventBus
import com.example.workouttracker.ui.managers.PermissionHandler
import com.example.workouttracker.ui.managers.ImagePickerManager
import com.example.workouttracker.viewmodel.AuthViewModel
import com.example.workouttracker.viewmodel.MainViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The main activity of the application */
@AndroidEntryPoint
class MainActivity: ComponentActivity(), IPermissionHost {
    private val vm by viewModels<MainViewModel>()
    private val authVm by viewModels<AuthViewModel>()
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var imagePickerManager: ImagePickerManager

    @Inject
    lateinit var imagePickerBus: ImagePickerEventBus

    override fun getLifecycleScope() = lifecycleScope
    override fun getPackageName(): String {
        return super.getPackageName()
    }

    override fun shouldShowRationaleForPermission(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
    }

    override fun registerPermissionLauncher(callback: (Boolean) -> Unit) =
        registerForActivityResult(ActivityResultContracts.RequestPermission(), callback)

    override fun registerActivityResultLauncher(callback: (ActivityResult) -> Unit) =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), callback)

    override fun registerPhotoPickerLauncher(callback: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
        return registerForActivityResult(ActivityResultContracts.PickVisualMedia(), callback)
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
    }

    override fun checkPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun getContentResolver(): ContentResolver {
        return super.getContentResolver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Show splash screen with animation
        splashScreen()

        super.onCreate(savedInstanceState)

        // Initialize the classes dependent on activity methods
        permissionHandler = PermissionHandler(
            permHost = this,
            askForAll = vm.sharedPrefsManager.isFirstAppStart(),
            showQuestion = { vm.showAllowCameraQuestion { permissionHandler.goToCameraSettings() } },
            showSnackbar = {
                lifecycleScope.launch {
                    vm.snackbarManager.showSnackbar(it)
                }
            },
            logManager = vm.systemLogManager
        )
        imagePickerManager = ImagePickerManager(permissionHandler, vm.askQuestionManager)

        setContent {
            Screen(vm = vm)
        }

        // Collect events
        askForAllPermissions()
        showImagePicker()
    }

    /** Show animated splash screen until the token is validated */
    private fun splashScreen() {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !authVm.tokenValidated.value
            }
            setOnExitAnimationListener { screen ->
                val iconView = try {
                    screen.iconView
                } catch (_: NullPointerException) {
                    screen.remove()
                    return@setOnExitAnimationListener
                }

                val zoomX = ObjectAnimator.ofFloat(
                    iconView,
                    View.SCALE_X,
                    0.5f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 1000L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    iconView,
                    View.SCALE_Y,
                    0.5f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 1000L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }
    }

    /** Ask user to grant all permissions */
    private fun askForAllPermissions() {
        vm.showAskForAllPermissions(
            onConfirm = {
                permissionHandler.cameraPermLauncher.launch(android.Manifest.permission.CAMERA)
            }
        )
    }

    /** Collect events to show image picker */
    private fun showImagePicker() {
        lifecycleScope.launch {
            imagePickerBus.imagePickerRequests.collect { picker ->
                imagePickerManager.showImagePicker(picker)
            }
        }
    }
}

