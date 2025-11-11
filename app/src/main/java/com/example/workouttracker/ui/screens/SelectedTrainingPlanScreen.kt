package com.example.workouttracker.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.components.TrainingDayItem
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.viewmodel.SelectedTrainingPlanViewModel

/** The screen to allow coaches to create /edit programs */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SelectedTrainingPlanScreen(vm: SelectedTrainingPlanViewModel = hiltViewModel()) {
    val selected by vm.trainingProgramRepository.selectedTrainingPlan.collectAsStateWithLifecycle()
    val selectedTrainingPlan = selected ?: return

    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize())
    {
        Column(modifier = Modifier.fillMaxWidth()) {
            Label(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingSmall),
                text = selectedTrainingPlan.name,
                style = labelLargeBold,
                maxLines = 4
            )

            if (selectedTrainingPlan.description.isNotEmpty()) {
                Label(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = PaddingSmall, end = PaddingSmall, bottom = PaddingSmall),
                    text = selectedTrainingPlan.description,
                    textAlign = TextAlign.Left,
                    maxLines = 5
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = PaddingSmall)
            )

            Label(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = PaddingVerySmall),
                text = stringResource(id = R.string.training_days),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = PaddingSmall, end = PaddingSmall, top = PaddingSmall),
                contentPadding = PaddingValues(bottom = LazyListBottomPadding + 10.dp),
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PaddingSmall)
            ) {

                itemsIndexed(selectedTrainingPlan.trainingDays) { index, item ->
                    TrainingDayItem(
                        trainingDay = item,
                        trainingDayIndex = index,
                        onEditClick = { model, rowNumber ->
                            vm.showAddEditTrainingDay(item, index + 1)
                        }
                    )
                }
            }
        }

        ImageButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = PaddingSmall),
            onClick = { vm.showEditTrainingPlan() },
            image = Icons.Default.Edit
        )

        ImageButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = PaddingSmall),
            onClick = { vm.showAddEditTrainingDay(null, 0) },
            image = Icons.Default.Add
        )
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun SelectedTrainingPlanScreenPreview() {
    WorkoutTrackerTheme {
        SelectedTrainingPlanScreen()
    }
}