package com.example.workouttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.viewmodel.Page
import kotlinx.coroutines.launch

@Composable
fun TopBar(drawerState: DrawerState) {
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications icon",
                colorFilter = ColorFilter.tint(color = ColorWhite),
            )
            Label(
                text = stringResource(id = R.string.notifications),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                scope.launch {
                    PagerManager.changePageSelection(Page.Actions)
                }
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
    MaterialTheme {
        TopBar(drawerState = rememberDrawerState(initialValue = DrawerValue.Closed))
    }
}