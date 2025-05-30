package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.layout.Column
import com.example.workouttracker.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
 * Dialog to show muscle group exercise description
 * @param mgExerciseModel the muscle group exercise
 * */
@Composable
fun MGExerciseDescDialog(mgExerciseModel: MGExerciseModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = PaddingLarge * 2)
        .heightIn(min = 100.dp, max = 300.dp)
        .verticalScroll(rememberScrollState())
    ) {
        if (mgExerciseModel.description.isNotEmpty()) {
            Label(
                modifier = Modifier.padding(horizontal = PaddingSmall),
                text = mgExerciseModel.description,
                textAlign = TextAlign.Start,
                maxLines = 1000
            )
        } else {
            Label(
                modifier = Modifier.padding(start = PaddingSmall, end = PaddingSmall, bottom = PaddingLarge),
                text = stringResource(R.string.no_exercise_description_lbl),
                maxLines = 3
            )
        }
    }
}

@Preview
@Composable
fun MGExerciseDescDialogPreview() {
    WorkoutTrackerTheme {
        MGExerciseDescDialog(MGExerciseModel(1L, "Wide pull up", "Grip the Bar\\r\\nStand under the pull-up bar and grab it with both hands wider than shoulder-width apart. Use an overhand grip (palms facing away from you). Engage your core and let your body hang with arms fully extended.\\r\\nPull Yourself Up\\r\\nPull your body upward by squeezing your back and shoulder muscles, focusing on bringing your chest up toward the bar. Avoid swinging or using momentum; keep the movement controlled.\\r\\nLower Back Down\\r\\nSlowly lower yourself back down until your arms are fully extended. Repeat for your desired number of reps, usually aiming for controlled, smooth movements to maximize back engagement.", 1L))
    }
}