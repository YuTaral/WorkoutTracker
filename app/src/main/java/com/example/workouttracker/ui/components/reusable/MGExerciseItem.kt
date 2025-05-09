package com.example.workouttracker.ui.components.reusable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
 * Single muscle group exercise item
 * @param mgExerciseModel the muscle group exercise model
 * @param onClick callback to execute on muscle group exercise item click
 */
@Composable
fun MGExerciseItem(mgExerciseModel: MGExerciseModel, onClick: (MGExerciseModel) -> Unit) {
    var showDescription by rememberSaveable { mutableStateOf(true) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (showDescription) 180f else 0f,
        label = "ArrowRotation"
    )

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(PaddingVerySmall)
        .clickable(
            enabled = true,
            onClick = {
                onClick(mgExerciseModel)
            }
        )
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = PaddingVerySmall)
        ) {
            Label(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = PaddingVerySmall),
                text = mgExerciseModel.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )

            ImageButton(
                modifier = Modifier
                    .size(SmallImageButtonSize)
                    .rotate(rotationAngle),
                onClick = { showDescription = !showDescription },
                image = Icons.Default.KeyboardArrowDown,
                size = SmallImageButtonSize
            )
        }

        AnimatedVisibility(
            visible = showDescription,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Label(
                text = mgExerciseModel.description,
                maxLines = 30,
                textAlign = TextAlign.Start
            )
        }

        HorizontalDivider(
            color = ColorBorder,
            thickness = 2.dp
        )
    }
}

@Preview
@Composable
fun MGExerciseItemPreview() {
    WorkoutTrackerTheme {
        MGExerciseItem(
            MGExerciseModel(0, "Wide pull ups", "Stand under the pull-up bar and grab it with both hands wider than shoulder-width apart. Use an overhand grip (palms facing away from you.", 1),
            onClick = {})
    }
}