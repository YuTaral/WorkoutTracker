package com.example.workouttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.NotificationItem
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.NotificationsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import com.example.workouttracker.ui.reusable.CustomCheckbox
import com.example.workouttracker.ui.theme.labelMediumGrey

/** Screen to display notifications to the user */
@Composable
fun NotificationsScreen(vm: NotificationsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData()
    }

    val notifications by vm.notificationRepository.notifications.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val showReviewed by vm.showReviewed.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(PaddingSmall)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PaddingSmall),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomCheckbox(
                checked = showReviewed,
                onValueChange = { vm.updateShowReviewed(it) },
                text = stringResource(id = R.string.show_reviewed_lbl),
            )
        }

        if (notifications.isEmpty()) {
            Label(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.no_new_notifications),
                style = labelMediumGrey,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState
            ) {
                items(notifications) {
                    NotificationItem(
                        notification = it,
                        onClick = { vm.onClick(it) },
                        onRemoveClick = { vm.removeNotification(it) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun NotificationsScreenPreview() {
    WorkoutTrackerTheme {
        NotificationsScreen()
    }
}