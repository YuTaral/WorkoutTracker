package com.example.workouttracker.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.R
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.LargeImageButtonSize
import com.example.workouttracker.ui.theme.PaddingVerySmall

/**
 * Single muscle group item
 * @param muscleGroup the muscle group model
 * @param onClick callback to execute on muscle group item click
 */
@Composable
@SuppressLint("DiscouragedApi")
fun MuscleGroupItem(muscleGroup: MuscleGroupModel, onClick: (Long) -> Unit) {
    val context = LocalContext.current
    var imgId = R.drawable.icon_mg_not_found
    val resourceId = remember(muscleGroup.imageName) {
        context.resources.getIdentifier(muscleGroup.imageName, "drawable", context.packageName)
    }

    if (resourceId != 0) {
        imgId = resourceId
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingVerySmall)
            .clickable(
                enabled = true,
                onClick = { onClick(muscleGroup.id) }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier.size(120.dp),
            painter = painterResource(id = imgId),
            contentDescription = null,
        )

        Label(
            text = muscleGroup.name
        )

        ImageButton(
            onClick = {},
            image = Icons.AutoMirrored.Filled.ArrowForward,
            size = LargeImageButtonSize,
            buttonColor = Color.Transparent,
            imageColor = ColorAccent
        )
    }
}

@Preview
@Composable
fun MuscleGroupItemPreview() {
    WorkoutTrackerTheme {
        MuscleGroupItem(
            MuscleGroupModel(
                idVal = 1,
                nameVal = "Back",
                imageVal = "icon_mg_back"),
            onClick = {})
    }
}