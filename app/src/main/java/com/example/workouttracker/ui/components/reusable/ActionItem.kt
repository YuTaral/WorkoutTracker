package com.example.workouttracker.ui.components.reusable

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.MediumImageButtonSize
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
* Single action item
* @param imageId the image id to display
* @param titleId the action name to display
* @param onClick callback to execute on item click
*/
@Composable
@SuppressLint("DiscouragedApi")
fun ActionItem(imageId: Int, titleId: Int, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingVerySmall)
                .clickable(
                    enabled = true,
                    onClick = { onClick() }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = imageId),
                contentDescription = null,
            )

            Label(
                text = stringResource(id = titleId)
            )

            ImageButton(
                onClick = {},
                image = Icons.AutoMirrored.Filled.ArrowForward,
                size = MediumImageButtonSize,
                buttonColor = Color.Transparent,
                imageColor = ColorAccent
            )
        }
        HorizontalDivider(
            color = ColorBorder,
            thickness = 1.dp
        )
    }
}

@Preview
@Composable
fun ActionItemPreview() {
    WorkoutTrackerTheme {
        ActionItem(R.drawable.icon_screen_manage_exercise, R.string.manage_exercises_lbl, {})
    }
}