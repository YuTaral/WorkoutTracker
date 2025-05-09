package com.example.workouttracker.ui.components.fragments

import androidx.compose.foundation.layout.Box
import com.example.workouttracker.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.components.reusable.InputField
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.viewmodel.SelectExerciseViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.ui.components.reusable.ImageButton
import com.example.workouttracker.ui.components.reusable.MGExerciseItem
import com.example.workouttracker.ui.components.reusable.MuscleGroupItem
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.viewmodel.Mode
import kotlinx.coroutines.flow.StateFlow

/**
 * The screen to display muscle groups and exercises when adding exercise to workout
 */
@Composable
fun SelectExerciseScreen(vm: SelectExerciseViewModel = hiltViewModel()) {
    DisposableEffect(Unit) {
        onDispose {
            vm.resetData()
        }
    }

    val mode by vm.mode.collectAsStateWithLifecycle()
    val searchTerm by vm.search.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingVerySmall)
    ) {

        Label(
            modifier = Modifier.fillMaxWidth(),
            text = if (mode == Mode.SELECT_MUSCLE_GROUP) stringResource(id = R.string.select_muscle_group_lbl)
                    else stringResource(id = R.string.select_exercise_lbl),
            style = MaterialTheme.typography.titleMedium,
        )

        InputField(
            value = searchTerm,
            modifier = Modifier.padding(PaddingVerySmall),
            label = stringResource(id = R.string.search_lbl),
            onValueChange = {
                vm.updateSearch(it)
            }
        )

        if (mode == Mode.SELECT_MUSCLE_GROUP) {
            MuscleGroupsScreen(
                data = vm.filteredMuscleGroups,
                onClick = { vm.changeSelectedMuscleGroup(it) }
            )
        } else {
            ExercisesScreen(
                data = vm.filteredmGExercises,
                onClick = { vm.selectMGExercise(it) },
                onBackClick = { vm.changeSelectedMuscleGroup(0) }
            )
        }
    }
}

/**
 * Screen to display the muscle groups
 * @param data the muscle groups
 * @param onClick callback to execute on item click
 */
@Composable
private fun MuscleGroupsScreen(data: StateFlow<MutableList<MuscleGroupModel>>, onClick: (Long) -> Unit) {
    val muscleGroups by data.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
    ) {
        items(muscleGroups) { item ->
            MuscleGroupItem(item, onClick)
        }
    }
}

/**
 * Screen to display the muscle groups exercises
 * @param data the muscle groups exercises
 * @param onClick callback to execute on item click
 * @param onBackClick callback to execute on back button click
 */
@Composable
private fun ExercisesScreen(data: StateFlow<MutableList<MGExerciseModel>>, onClick: (MGExerciseModel) -> Unit, onBackClick: () -> Unit) {
    val mGExercises by data.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(bottom = LazyListBottomPadding)
        ) {
            items(mGExercises) { item ->
                MGExerciseItem(item, onClick)
            }
        }

        ImageButton(
            modifier = Modifier.align(Alignment.BottomStart),
            onClick = onBackClick,
            image = Icons.AutoMirrored.Filled.ArrowBack
        )

        ImageButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { },
            image = Icons.Default.Add
        )
    }
}
