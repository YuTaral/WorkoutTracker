package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.commandiron.compose_loading.ChasingDots
import com.example.workouttracker.R
import com.example.workouttracker.ui.theme.ColorLoadingDialogBackground
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/** Custom loading spinner used when request is being processed */
@Composable
fun RequestInProgressSpinner() {
    Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {

        Column(
            modifier = Modifier.border(
                width = 1.dp,
                color = ColorLoadingDialogBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(ColorLoadingDialogBackground)
            .padding(PaddingMedium * 2),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ChasingDots(
                modifier = Modifier.padding(bottom = PaddingLarge),
                color = ColorWhite,
                size = 30.dp
            )
            Label(
                text = stringResource(R.string.request_loading),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun CustomLoadingSpinnerPreview(modifier: Modifier = Modifier) {
    WorkoutTrackerTheme {
        RequestInProgressSpinner()
    }
}