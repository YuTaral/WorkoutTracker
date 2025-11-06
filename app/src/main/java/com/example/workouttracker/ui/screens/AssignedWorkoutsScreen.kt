package com.example.workouttracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.ui.components.AssignedWorkoutItem
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import com.example.workouttracker.ui.theme.labelMediumBold
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.AssignedWorkoutsViewModel

/**
 * Screen to allow coaches to view assigned workouts
 * @param team the team to filter by, null if not used
 */
@Composable
fun AssignedWorkoutsScreen(
    team: TeamModel? = null,
    vm: AssignedWorkoutsViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        vm.initializeData(team = team)
    }

    val lazyListState = rememberLazyListState()
    val workouts by vm.assignedWorkouts.collectAsStateWithLifecycle()
    val teamTypeFilter by vm.selectedTeamType.collectAsStateWithLifecycle()
    val startDateFilter by vm.startDate.collectAsStateWithLifecycle()
    val teamFilter by vm.teamFilter.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingSmall, vertical = PaddingVerySmall)
                .clickable(
                    enabled = true,
                    onClick = { vm.showFiltersDialog() }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Label(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.selected_filters),
                            style = labelMediumGrey,
                            textAlign = TextAlign.Left
                        )

                        ImageButton(
                            modifier = Modifier.size(SmallImageButtonSize),
                            onClick = {  vm.showFiltersDialog() },
                            image = Icons.Default.Edit,
                        )
                    }

                    Label(
                        text = stringResource(id = teamTypeFilter.getStringId()),
                        style = labelMediumBold,
                    )

                    Label(
                        text = String.format(stringResource(id = R.string.selected_filters_from_date),
                                            Utils.defaultFormatDate(startDateFilter)),
                        style = labelMediumBold,
                    )

                    if (teamFilter.id != 0L) {
                        Label(
                            text = teamFilter.name,
                            style = labelMediumBold,
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = PaddingSmall)
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
                            onClick = { vm.onClick(it) },
                        )
                    }
                }
            }
        }
    }
}
