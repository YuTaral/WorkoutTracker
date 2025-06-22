package com.example.workouttracker.ui.dialogs

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.theme.ColorDialogBackground
import com.example.workouttracker.ui.theme.BottomSheetsDialogFooterSize
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall

/**
 * Bottom sheets dialog to ask user for confirmation
 * @param event the event containing the question and any data needed to ask the question
 * and execute the callback
 * @param hideQuestion callback to execute to hide the question dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskQuestionDialog(event: DisplayAskQuestionDialogEvent, hideQuestion: () -> Unit) {
    val q = event.question!!
    val state = rememberModalBottomSheetState()
    val questionText = String.format(
        stringResource(id = q.getQuestionText()),
        *event.formatQValues.toTypedArray()
    )

    ModalBottomSheet(
        sheetState = state,
        containerColor = ColorDialogBackground,
        dragHandle = {},
        onDismissRequest = { hideQuestion() }
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
                text = String.format(stringResource(id = q.getTitle(), event.formatTitle)),
                style = MaterialTheme.typography.titleLarge
            )
            Label(
                modifier = Modifier.padding(PaddingSmall),
                text = questionText,
                maxLines = 10
            )

            Row(modifier = Modifier.fillMaxWidth().height(BottomSheetsDialogFooterSize)) {
                DialogButton(
                    modifier = Modifier
                        .weight(1f)
                        .customBorder(end = true),
                    text = stringResource(id = q.getCancelButtonText()),
                    onClick = {
                        event.onCancel()
                        hideQuestion()
                    }
                )
                DialogButton(
                    modifier = Modifier
                        .weight(1f)
                        .customBorder(),
                    text = stringResource(id = q.getConfirmButtonText()),
                    onClick = {
                        event.onConfirm()
                        hideQuestion()
                    }
                )
            }
        }
    }
}
