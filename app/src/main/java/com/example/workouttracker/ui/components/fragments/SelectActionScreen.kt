package com.example.workouttracker.ui.components.fragments

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.reusable.ActionItem
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.Page
import kotlinx.coroutines.launch

/** Different actions accessed from the actions menu */
sealed class Action(val imageId: Int, val titleId: Int, val onClick: suspend () -> Unit ) {
    data object ManageExercises : Action(R.drawable.icon_screen_manage_exercise, R.string.manage_exercises_lbl,
        { PagerManager.changePageSelection(Page.ManageExercise) })
    data object ManageTemplates : Action(R.drawable.icon_screen_manage_templates, R.string.manage_templates_lbl,
        { PagerManager.changePageSelection(Page.ManageTemplates) })
}

@Composable
fun SelectActionScreen() {
    val actions = listOf<Action>(
        Action.ManageTemplates,
        Action.ManageExercises
    )
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
       items(actions) { item ->
           ActionItem(
               imageId = item.imageId,
               titleId = item.titleId,
               onClick = {
                   scope.launch {
                       item.onClick()
                   }
               }
           )
       }
    }
}

@Preview
@Composable
private fun SelectActionScreenPreview() {
    WorkoutTrackerTheme {
        SelectActionScreen()
    }
}