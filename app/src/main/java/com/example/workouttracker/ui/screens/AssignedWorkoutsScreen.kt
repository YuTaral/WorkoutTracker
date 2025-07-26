package com.example.workouttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.AssignedWorkoutItem
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.reusable.Spinner
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.AssignedWorkoutsViewModel

/**
 * Screen to allow coaches to view assigned workouts
 * @param teamId the team id (0 if not used)
 */
@Composable
fun AssignedWorkoutsScreen(teamId: Long, vm: AssignedWorkoutsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData(teamId = teamId)
    }

    val workouts by vm.assignedWorkouts.collectAsStateWithLifecycle()
    val user by vm.userRepository.user.collectAsStateWithLifecycle()
    val myTeams by vm.teamRepository.teams.collectAsStateWithLifecycle()
    val startDateFilter by vm.startDate.collectAsStateWithLifecycle()
    val teamFilter by vm.teamFilter.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PaddingSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Label(
                        text = String.format(
                            stringResource(id = R.string.workouts_filter_lbl),
                            Utils.defaultFormatDate(startDateFilter)
                        ),
                    )

                    ImageButton(
                        onClick = { vm.showDatePicker() },
                        image = Icons.Default.DateRange
                    )
                }
            }

            Spinner(
                modifier = Modifier.padding(horizontal = PaddingSmall),
                items = myTeams.associate { it.id.toString() to it.name },
                selectedItemKey = teamFilter.id.toString(),
                onItemSelected = {
                    vm.updateTeamFilter(it)
                }
            )

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(PaddingSmall)
            )

            if (workouts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PaddingMedium)
                ) {
                    Label(
                        text = stringResource(id = R.string.no_workouts_with_filters),
                        style = labelMediumGrey,
                        maxLines = 2
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(bottom = LazyListBottomPadding)
                ) {
                    items(workouts) { item ->
                        AssignedWorkoutItem(
                            assignedWorkout = item,
                            weightUnit = user!!.defaultValues.weightUnit.text,
                            onClick = {  },
                        )
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun DrawerContentPreview() {
    WorkoutTrackerTheme {
        AssignedWorkoutsScreen(0L)
    }
}