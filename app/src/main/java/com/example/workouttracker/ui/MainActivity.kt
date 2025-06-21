package com.example.workouttracker.ui

import android.animation.ObjectAnimator
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
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.PermissionHandler
import com.example.workouttracker.ui.managers.ImageUploadManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.MainViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/** The main activity of the application */
@AndroidEntryPoint
class MainActivity: ComponentActivity(), PermissionHost {
    private val vm by viewModels<MainViewModel>()
    private lateinit var permissionHandler: PermissionHandler

    override fun getLifecycleScope() = lifecycleScope
    override fun getPackageName(): String {
        return super.getPackageName()
    }

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
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
        startActivity(intent)
    }

    override fun checkPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Show splash screen with animation
        splashScreen()

        super.onCreate(savedInstanceState)

        // Initialize the classes dependent on activity methods
        Utils.init(this)
        permissionHandler = PermissionHandler(this, vm.sharedPrefsManager.isFirstAppStart())
        ImageUploadManager.init(permissionHandler)

        setContent {
            Screen(vm = vm)
        }

        // Collect events
        askForAllPermissions()
    }

    /** Show animated splash screen until the token is validated */
    private fun splashScreen() {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !vm.tokenValidated.value
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.5f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 1000L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
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
        lifecycleScope.launch {
            vm.userRepository.requestPermissions.collect {
                vm.viewModelScope.launch {
                    AskQuestionDialogManager.askQuestion(DisplayAskQuestionDialogEvent(
                        question = Question.GRANT_PERMISSIONS,
                        show = true,
                        onConfirm = {
                            permissionHandler.cameraPermLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    ))
                }
            }
        }
    }
}

