package com.example.workouttracker.ui.screens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.viewmodel.SelectExerciseViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.ui.reusable.Spinner
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.components.MGExerciseItem
import com.example.workouttracker.ui.components.MuscleGroupItem
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.viewmodel.SelectExerciseViewModel.Mode
import kotlinx.coroutines.flow.StateFlow

/**
 * The screen to display muscle groups and exercises when adding exercise to workout or managing exercises
 * @param manageExercises true if the screen is in manage exercise mode (add/update/delete), false
 * otherwise - to select exercises and add to workout
 */
@Composable
fun SelectExerciseScreen(manageExercises: Boolean, vm: SelectExerciseViewModel = hiltViewModel()) {
    DisposableEffect(Unit) {
        onDispose {
            vm.resetData()
        }
    }

    LaunchedEffect(Unit) {
        vm.initializeData(manageExercises)
    }

    val mode by vm.mode.collectAsStateWithLifecycle()
    val searchTerm by vm.searchHelper.search.collectAsStateWithLifecycle()
    val manageExercises by vm.manageExercises.collectAsStateWithLifecycle()
    val selectedAction by vm.selectedSpinnerAction.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingVerySmall)
    ) {

        if (mode == Mode.SELECT_EXERCISE && manageExercises) {

            Spinner(
                modifier = Modifier.padding(horizontal = PaddingVerySmall),
                items = vm.spinnerActions.associate { it.name to stringResource(id = it.getStringId()) },
                selectedItemKey = selectedAction!!.name,
                onItemSelected = {
                    vm.updateSelectedSpinnerAction(it)
                }
            )
        } else {
            Label(
                modifier = Modifier.fillMaxWidth(),
                text = if (mode == Mode.SELECT_MUSCLE_GROUP) stringResource(id = R.string.select_muscle_group_lbl)
                else stringResource(id = R.string.select_exercise_lbl),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        InputField(
            value = searchTerm,
            modifier = Modifier.padding(PaddingVerySmall),
            label = stringResource(id = R.string.search_lbl),
            onValueChange = { vm.searchHelper.updateSearchTerm(it) }
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
                onBackClick = { vm.changeSelectedMuscleGroup(0) },
                manageExercises = manageExercises,
                onAddClick = { vm.showAddMGExercise() },
                searchEmpty = searchTerm.isEmpty()
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
 * @param manageExercises whether the screen is in manage mode
 * @param onAddClick callback to execute on Add button click
 * @param
 */
@Composable
private fun ExercisesScreen(data: StateFlow<MutableList<MGExerciseModel>>,
                            onClick: (MGExerciseModel) -> Unit,
                            onBackClick: () -> Unit,
                            manageExercises: Boolean,
                            onAddClick: () -> Unit,
                            searchEmpty: Boolean
) {
    val mGExercises by data.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {

        if (manageExercises && mGExercises.isEmpty()) {
            val noExercisesErrorId = if (searchEmpty) {
                R.string.no_exercises_to_edit
            } else {
                R.string.no_exercises_matching
            }

            Label(
                modifier = Modifier.padding(PaddingSmall),
                text = stringResource(id = noExercisesErrorId),
                style = labelMediumGrey,
                maxLines = 4
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = PaddingValues(bottom = LazyListBottomPadding)
            ) {
                items(mGExercises) { item ->
                    MGExerciseItem(item, onClick)
                }
            }
        }

        ImageButton(
            modifier = Modifier.align(Alignment.BottomStart),
            onClick = onBackClick,
            image = Icons.AutoMirrored.Filled.ArrowBack
        )

        ImageButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { onAddClick() },
            image = Icons.Default.Add
        )
    }
}
