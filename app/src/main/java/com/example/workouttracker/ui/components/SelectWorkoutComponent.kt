package com.example.workouttracker.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.workouttracker.R
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.labelMediumGrey

/**
 * Composable to select a workout from the list of templates
 * @param templates The list of workout templates to select from
 * @param onClick The callback when a workout is selected
 */
@Composable
fun SelectWorkoutComponent(templates: List<WorkoutModel>, onClick: (WorkoutModel) -> Unit) {
    val lazyListState = rememberLazyListState()

    Label(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = PaddingSmall),
        text = stringResource(id = R.string.select_workout_lbl),
        style = MaterialTheme.typography.titleSmall
    )

    if (templates.isEmpty()) {
        Label(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.no_templates),
            textAlign = TextAlign.Center,
            style = labelMediumGrey,
            maxLines = 5
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(bottom = LazyListBottomPadding)
        ) {
            items(templates) { item ->
                WorkoutItem(
                    workout = item,
                    onClick = { onClick(it) }
                )
            }
        }
    }
}