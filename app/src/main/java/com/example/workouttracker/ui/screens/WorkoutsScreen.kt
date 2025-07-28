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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.components.WorkoutItem
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.WorkoutsViewModel

@Composable
/**
 * Main Screen to display the latest workouts
 */
fun WorkoutsScreen(vm: WorkoutsViewModel = hiltViewModel()) {
    val workouts by vm.workoutRepository.workouts.collectAsStateWithLifecycle()
    val user by vm.userRepository.user.collectAsStateWithLifecycle()
    val startDate by vm.startDate.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        Utils.defaultFormatDate(startDate)
                    ),
                )

                ImageButton(
                    onClick = { vm.showDatePicker() },
                    image = Icons.Default.DateRange
                )
            }

            HorizontalDivider(color = ColorBorder, thickness = 1.dp)

            if (workouts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PaddingMedium)
                ) {
                    Label(
                        text = stringResource(id = R.string.no_workouts),
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
                        WorkoutItem(
                            workout = item,
                            weightUnit = user!!.defaultValues.weightUnit.text,
                            onClick = { vm.selectWorkout(item) }
                        )
                    }
                }
            }
        }

        ImageButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = PaddingSmall),
            onClick = { vm.showAddWorkoutDialog() },
            image = Icons.Default.Add
        )
    }
}
