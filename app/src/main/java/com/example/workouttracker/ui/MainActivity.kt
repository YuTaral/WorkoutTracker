package com.example.workouttracker.ui

import android.animation.ObjectAnimator
import com.example.workouttracker.ui.screens.Screen
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.workouttracker.ui.managers.PermissionResultHandler
import com.example.workouttracker.ui.managers.ImageUploadManager
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.MainViewModel

import dagger.hilt.android.AndroidEntryPoint

/** The main activity of the application */
@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    private val vm by viewModels<MainViewModel>()
    private val permissionResultHandler = PermissionResultHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Show splash screen with animation
        splashScreen()

        super.onCreate(savedInstanceState)
        Utils.init(this)
        ImageUploadManager.init(permissionResultHandler)

        setContent {
            Screen(vm = vm)
        }
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
}

