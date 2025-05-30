package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.EditProfileViewModel
import androidx.compose.runtime.getValue
import com.example.workouttracker.ui.reusable.ErrorLabel

/** Edit profile dialog to allow the user to change name / profile picture */
@Composable
fun EditProfileDialog(vm: EditProfileViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeState()
    }

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Column(modifier =
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.profileImage.isEmpty()) {
            Image(
                imageVector = Icons.Default.Person,
                colorFilter = ColorFilter.tint(color = ColorWhite),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(2.dp, ColorBorder, CircleShape)
                    .padding(PaddingSmall/2)
                    .clickable(
                        enabled = true,
                        onClick = { vm.onImageClick() }
                    ),
                contentDescription = "Profile image",
            )
        } else {
            val profileImagePainter = remember(uiState.profileImage) {
                val bitmap = Utils.convertStringToBitmap(uiState.profileImage)
                BitmapPainter(bitmap.asImageBitmap())
            }

            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(2.dp, ColorBorder, CircleShape)
                    .clickable(
                        enabled = true,
                        onClick = { vm.onImageClick() }
                    ),
                painter = profileImagePainter,
                contentDescription = "Profile image"
            )
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(DialogFooterSize)
        ) {
            DialogButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.remove_picture_btn),
                onClick = { vm.updateImage("") }
            )
        }

        InputField(modifier = Modifier.padding(horizontal = PaddingSmall),
            label = stringResource(id = R.string.full_name_lbl),
            value = uiState.fullName,
            onValueChange = {
                if (it.length < 100) {
                    vm.updateName(it)
                }
            },
            isError = uiState.fullNameError != null
        )

        uiState.fullNameError?.let {
            ErrorLabel(
                modifier = Modifier.padding(end = PaddingSmall),
                text = uiState.fullNameError!!
            )
        }

        Row(modifier = Modifier
            .padding(top = PaddingLarge)
            .fillMaxWidth()
            .height(DialogFooterSize)
        ) {
            DialogButton(
                modifier = Modifier
                    .customBorder()
                    .weight(1f),
                text = stringResource(R.string.save_btn),
                onClick = { vm.save() }
            )
        }
    }
}

@Preview
@Composable
private fun EditProfilePreview() {
    WorkoutTrackerTheme {
        EditProfileDialog()
    }
}