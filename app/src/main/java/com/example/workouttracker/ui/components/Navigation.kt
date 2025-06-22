package com.example.workouttracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workouttracker.ui.screens.AuthScreen
import com.example.workouttracker.ui.screens.MainScreen
import com.example.workouttracker.viewmodel.MainViewModel
import com.example.workouttracker.viewmodel.Page

/** Navigation destinations */
enum class Destinations {
    AUTH,
    MAIN
}

/**
 * The main app navigation
 * @param modifier the default modifier
 * @param vm the main screen view model
 */
@Composable
fun Navigation(modifier: Modifier, vm: MainViewModel) {
    val navController = rememberNavController()
    val user by vm.userRepository.user.collectAsStateWithLifecycle()
    val notification by vm.notificationRepository.notification.collectAsStateWithLifecycle()

    LaunchedEffect(user) {
        // Check if the current destination is already the one we're trying to navigate to
        val currentRoute = navController.currentDestination?.route

        when {
            user != null && currentRoute != Destinations.MAIN.name -> {
                navController.navigate(Destinations.MAIN.name) {
                    popUpTo(Destinations.AUTH.name) { inclusive = true }
                }
            }
            user == null && currentRoute != Destinations.AUTH.name -> {
                navController.navigate(Destinations.AUTH.name) {
                    popUpTo(Destinations.MAIN.name) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Destinations.AUTH.name,
        modifier = modifier
    ) {
        composable(route = Destinations.AUTH.name) {
            AuthScreen()
        }
        composable(route = Destinations.MAIN.name) {
            user?.let {
                MainScreen(
                    email = it.email,
                    fullName = it.fullName,
                    profileImage = it.profileImage,
                    notification = notification,
                    displayNotifications = { vm.changePage(Page.Notifications)},
                    displayActions = { vm.changePage(Page.Actions) }
                )
            }
        }
    }
}
