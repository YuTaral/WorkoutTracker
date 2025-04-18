package com.example.workouttracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.ui.components.DrawerContent
import com.example.workouttracker.ui.components.TopBar
import com.example.workouttracker.ui.components.Pager
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/** The application main screen displayed after login */
@Composable
fun MainScreen(
        email: String,
        fullName: String,
        profileImage: String
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                    DrawerContent(
                        email = email,
                        fullName = fullName,
                        profileImage = profileImage,
                        drawerState
                    )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TopBar(drawerState)
                Pager()
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun DefaultPreview() {
    WorkoutTrackerTheme {
        MainScreen("test@abv.bg", "Test user", "")
    }
}
