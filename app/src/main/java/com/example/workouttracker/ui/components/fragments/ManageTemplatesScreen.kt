package com.example.workouttracker.ui.components.fragments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.reusable.InputField
import com.example.workouttracker.ui.components.reusable.Spinner
import com.example.workouttracker.ui.components.reusable.WorkoutItem
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.ManageTemplatesViewModel

@Composable
fun ManageTemplatesScreen(vm: ManageTemplatesViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData()
    }

    val lazyListState = rememberLazyListState()
    val selectedAction by vm.selectedSpinnerAction.collectAsStateWithLifecycle()
    val searchTerm by vm.search.collectAsStateWithLifecycle()
    val workouts by vm.filteredTemplates.collectAsStateWithLifecycle()
    val user by vm.userRepository.user.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(PaddingVerySmall)
        ) {
            Spinner(
                modifier = Modifier.padding(horizontal = PaddingVerySmall),
                items = vm.spinnerActions.map { stringResource(id = it.getStringId()) },
                selectedItem = stringResource(id = selectedAction.getStringId()),
                onItemSelected = {
                    vm.updateSelectedSpinnerAction(it)
                }
            )

            InputField(
                value = searchTerm,
                modifier = Modifier.padding(PaddingVerySmall),
                label = stringResource(id = R.string.search_lbl),
                onValueChange = {
                    vm.updateSearch(it)
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = PaddingValues(bottom = LazyListBottomPadding)
            ) {
                items(workouts) { item ->
                    WorkoutItem(
                        workout = item,
                        weightUnit = user!!.defaultValues.weightUnit.text,
                        onClick = { vm.selectTemplate(it) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ManageTemplatesScreenPreview() {
    WorkoutTrackerTheme {
        ManageTemplatesScreen()
    }
}