package com.example.workouttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import kotlinx.coroutines.launch

/**
 * The top bar of the application
 * @param drawerState the left drawer state
 * @param notification whether the user has new notifications
 * @param displayNotifications display the notifications screen
 * @param displayActions display the actions screen
 */
@Composable
fun TopBar(
    drawerState: DrawerState,
    notification: Boolean,
    displayNotifications: () -> Unit,
    displayActions: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.clickable {
                scope.launch {
                    drawerState.open()
                }
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = "Person icon",
                colorFilter = ColorFilter.tint(color = ColorWhite),
            )
            Label(
                text = stringResource(id = R.string.profile),
            )
        }
        Column(
            modifier = Modifier.clickable(
                enabled = true,
                onClick = { displayNotifications() }
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Image(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications icon",
                    colorFilter = ColorFilter.tint(color = ColorWhite),
                )

                if (notification) {
                    Image(
                        modifier = Modifier
                            .size(15.dp)
                            .align(Alignment.TopEnd),
                        painter = painterResource(R.drawable.icon_notification_circle),
                        contentDescription = "Notifications icon",
                    )
                }
            }

            Label(
                text = stringResource(id = R.string.notifications),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                scope.launch { displayActions() }
            },
        ) {
            Image(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu icon",
                colorFilter = ColorFilter.tint(color = ColorWhite),
            )
            Label(
                text = stringResource(id = R.string.actions),
            )
        }
    }
    HorizontalDivider(color = ColorBorder, thickness = 2.dp)
}

@Preview(widthDp = 360)
@Composable
private fun TopBarPreview() {
    WorkoutTrackerTheme {
        TopBar(drawerState = rememberDrawerState(initialValue = DrawerValue.Closed), true, {}, {})
    }
}