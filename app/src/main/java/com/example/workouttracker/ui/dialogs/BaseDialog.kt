package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorDialogBackground
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
 * Composable function to display a dialog
 * @param title the dialog title
 * @param dialogName the dialog name
 * @param content the dialog content
 * @param hideDialog hide the dialog on close click
 */
@Composable
fun BaseDialog(
    title: String,
    dialogName: String,
    content: @Composable () -> Unit,
    hideDialog: (String) -> Unit,
) {

    Dialog(
        onDismissRequest = { },
        DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingMedium)
                .border(
                    width = 1.dp,
                    color = ColorDialogBackground,
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(ColorDialogBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = PaddingSmall),
                contentAlignment = Alignment.CenterStart
            ) {
                Label(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart),
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )

                Button(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    onClick = { hideDialog(dialogName) }
                ) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Default.Close,
                        colorFilter = ColorFilter.tint(color = ColorAccent),
                        contentDescription = "Close dialog",
                    )
                }
            }
            content()
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun BaseDialogPreview() {
    WorkoutTrackerTheme {
        BaseDialog(
            title = "Dialog title",
            dialogName = "Test",
            content = {
                Label(text = "Dialog content goes here")
            },
            hideDialog = {}
        )
    }
}