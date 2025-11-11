package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TrainingDayModel
import com.example.workouttracker.ui.components.SelectTemplateItem
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingMedium

import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

import com.example.workouttracker.viewmodel.AddEditTrainingDayViewModel


/** Edit profile dialog to allow the user to add / edit training day */
@Composable
fun AddEditTrainingDayDialog(model: TrainingDayModel, vm: AddEditTrainingDayViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData(model = model)
    }

    val lazyListState = rememberLazyListState()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            state = lazyListState,
        ) {
            items(uiState.templatesState) { item ->
                SelectTemplateItem(
                    template = item,
                    onClick = { vm.changeTemplateSelected(item) }
                )
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = PaddingMedium)
            .height(DialogFooterSize)
        ) {

            if (model.id > 0) {
                DialogButton(
                    modifier = Modifier
                        .customBorder()
                        .weight(1f),
                    text = stringResource(R.string.delete_btn),
                    onClick = { vm.askDelete() }
                )
            }

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
private fun EditTrainingDayPreview() {
    WorkoutTrackerTheme {
        AddEditTrainingDayDialog(TrainingDayModel(0))
    }
}
