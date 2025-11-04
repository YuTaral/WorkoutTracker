package com.example.workouttracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TrainingPlanModel
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.ui.theme.labelMediumItalic

/**
 * Single training program displayed in the manage training programs screen
 * @param trainingPlan the training program model
 * @param onClick callback to execute on training program click
 */
@Composable
fun TrainingPlanItem(
    trainingPlan: TrainingPlanModel,
    onClick: (TrainingPlanModel) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingVerySmall)
            .clickable(
                enabled = true,
                onClick = {
                    onClick(trainingPlan)
                }
            ),
    ) {
        Label(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingVerySmall),
            text = trainingPlan.name,
            style = labelLargeBold,
            maxLines = 2,
            textAlign = TextAlign.Left
        )

        if (trainingPlan.trainingDays.isNotEmpty()) {
            for (day in trainingPlan.trainingDays) {
                if (day.workouts.isEmpty()) {
                    Label(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = PaddingVerySmall),
                        text = "- " + stringResource(id = R.string.rest),
                        textAlign = TextAlign.Left,
                        style = labelMediumItalic
                    )
                } else {
                    Label(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = PaddingVerySmall),
                        text = "- " + day.workouts.joinToString(", ") { workout -> workout.name },
                        textAlign = TextAlign.Left
                    )
                }
            }
        }

        if (trainingPlan.description.isNotEmpty()) {
            Label(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PaddingVerySmall, end = PaddingVerySmall, top = PaddingVerySmall),
                text = trainingPlan.description,
                maxLines = 4,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.labelSmall
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = PaddingVerySmall),
            color = ColorBorder,
            thickness = 1.dp
        )
    }
}

@Preview
@Composable
fun TrainingProgramItemPreview() {
    WorkoutTrackerTheme {
        TrainingPlanItem(
            TrainingPlanModel(1, "The best program ever", "A description"),
            onClick = {}
        )
    }
}