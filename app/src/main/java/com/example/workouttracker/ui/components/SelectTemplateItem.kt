package com.example.workouttracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.data.models.ExerciseModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.reusable.CustomCheckbox
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.viewmodel.AddEditTrainingDayViewModel.TemplateUIState
import java.util.Date

/**
 * Single workout/template displayed in the workouts screen
 * @param template the workout model
 * @param onClick callback to execute on workout click
 */
@Composable
fun SelectTemplateItem(
    template: TemplateUIState,
    onClick: (TemplateUIState) -> Unit
) {

    var showExercise by rememberSaveable { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (showExercise) 180f else 0f,
        label = "ArrowRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingVerySmall)
            .clickable(
                enabled = true,
                onClick = {
                    onClick(template)
                }),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomCheckbox(
                checked = template.selected,
                onValueChange = { onClick(template) },
                text = ""
            )

            Label(
                modifier = Modifier
                    .weight(1f),
                text = template.template.name,
                style = labelLargeBold,
                maxLines = 2,
            )

            ImageButton(
                modifier = Modifier
                    .padding(bottom = PaddingVerySmall)
                    .size(SmallImageButtonSize)
                    .rotate(rotationAngle),
                onClick = { showExercise = !showExercise },
                image = Icons.Default.KeyboardArrowDown,
                size = SmallImageButtonSize
            )
        }

        AnimatedVisibility(
            visible = showExercise,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                for (e: ExerciseModel in template.template.exercises) {
                    Label(
                        modifier = Modifier.fillMaxWidth(),
                        text = String.format(stringResource(id = R.string.exercise_summary), e.name, e.sets.size),
                        style = labelMediumGrey,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
        HorizontalDivider(color = ColorBorder, thickness = 1.dp)
    }
}

@Preview
@Composable
fun SelectTemplateItemPreview() {
    WorkoutTrackerTheme {
        SelectTemplateItem(TemplateUIState(
            WorkoutModel(
            idVal = 0,
            nameVal = "Back day",
            templateVal = false,
            exercisesVal = mutableListOf(),
            finishDateTimeVal = Date(),
            notesVal = "This is the best back day",
            durationVal = null)
        ),
            onClick = {})
    }
}