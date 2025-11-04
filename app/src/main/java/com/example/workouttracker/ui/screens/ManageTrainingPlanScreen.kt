package com.example.workouttracker.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.ManageTrainingPlanViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.TrainingPlanItem
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.labelMediumGrey

/** The screen to allow coaches to create programs */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ManageTrainingPlanScreen(vm: ManageTrainingPlanViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData()
    }

    val trainingPrograms by vm.trainingProgramRepository.trainingPlans.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize())
    {
        if (trainingPrograms.isEmpty()) {
            Label(
                modifier = Modifier.padding(PaddingSmall),
                text = stringResource(id = R.string.no_training_programs),
                style = labelMediumGrey,
                maxLines = 4
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = PaddingValues(bottom = LazyListBottomPadding)
            ) {
                items(trainingPrograms) { item ->
                    TrainingPlanItem(
                        trainingPlan = item,
                        onClick = { vm.selectTrainingProgram(item) }
                    )
                }
            }
        }

        ImageButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = PaddingSmall),
            onClick = { vm.showAddTrainingPlan() },
            image = Icons.Default.Add
        )
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun AssignWorkoutScreenPreview() {
    WorkoutTrackerTheme {
        ManageTrainingPlanScreen()
    }
}