package com.example.workouttracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.workouttracker.ui.components.DrawerContent
import com.example.workouttracker.ui.components.TopBar
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.MainViewModel

/** The application main screen displayed after login */
@Composable
fun MainScreen(vm: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MainContent(vm = vm)
    }
}

/** The application main content */
@Composable
fun MainContent(vm: MainViewModel) {
    val user = remember { vm.user.value }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(user!!, drawerState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopBar(drawerState)
            AppLogo()
            Label(text = "HELLO ${user?.fullName}, YOU LOGGED IN SUCCESSFULLY", modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun DefaultPreview() {
    WorkoutTrackerTheme {
        MainScreen(vm = hiltViewModel())
    }
}
