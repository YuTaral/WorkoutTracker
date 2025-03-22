package com.example.workouttracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import com.example.workouttracker.ui.components.reusable.RequestInProgressSpinner
import com.example.workouttracker.ui.components.Navigation
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.managers.LoadingManager
import com.example.workouttracker.ui.managers.SnackbarManager
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.viewmodel.MainViewModel

/**
 * The application root screen containing the navigation and the logic to show snackbar, make vibrations
 * and show dialogs
 */
@Composable
fun Screen(vm: MainViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showLoading by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        WorkoutTrackerTheme {

            // Show loading
            LaunchedEffect(LoadingManager.events) {
                LoadingManager.events.collect { isLoading ->
                    showLoading = isLoading
                }
            }

            if (showLoading) {
                RequestInProgressSpinner()
            }

            // Show snackbar
            LaunchedEffect(SnackbarManager.events) {
                SnackbarManager.events.collect { event ->
                    snackbarHostState.currentSnackbarData?.dismiss()

                    val message = if (event.messageId > 0) getString(context, event.messageId)
                    else event.message

                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = null,
                        withDismissAction = true
                    )
                }
            }

            // Trigger vibrations
            LaunchedEffect(VibrationManager.events) {
                VibrationManager.events.collect { event ->
                    VibrationManager.makeVibration(context, event)
                }
            }

            Navigation(modifier = Modifier.padding(innerPadding), vm = vm)
        }
    }
}