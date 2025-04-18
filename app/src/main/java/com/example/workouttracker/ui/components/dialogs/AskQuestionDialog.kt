package com.example.workouttracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.workouttracker.ui.components.extensions.customBorder
import com.example.workouttracker.ui.components.reusable.DialogButton
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.ShowHideDialogEvent
import com.example.workouttracker.ui.theme.ColorDialogBackground
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingLarge
import kotlinx.coroutines.launch

/** Bottom sheets dialog to ask user for confirmation */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskQuestionDialog(event: ShowHideDialogEvent) {
    val q = event.question!!
    val state = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = state,
        containerColor = ColorDialogBackground,
        dragHandle = {},
        onDismissRequest = {
            scope.launch {
                AskQuestionDialogManager.hideQuestion()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = PaddingLarge)
                .background(ColorDialogBackground),
            verticalArrangement = Arrangement.spacedBy(PaddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Label(
                text = stringResource(id = q.getTitle()),
                style = MaterialTheme.typography.titleLarge
            )
            Label(
                text = stringResource(id = q.getQuestionText()),
            )

            Row(modifier = Modifier.fillMaxWidth().height(DialogFooterSize)) {
                DialogButton(
                    modifier = Modifier
                        .weight(1f)
                        .customBorder(end = true),
                    text = stringResource(id = q.getCancelButtonText()),
                    onClick = event.onCancel
                )
                DialogButton(
                    modifier = Modifier
                        .weight(1f)
                        .customBorder(),
                    text = stringResource(id = q.getConfirmButtonText()),
                    onClick = event.onConfirm
                )
            }
        }
    }
}
