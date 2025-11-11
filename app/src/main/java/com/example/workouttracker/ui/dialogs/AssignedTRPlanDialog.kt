package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TrainingPlanModel
import com.example.workouttracker.ui.components.TrainingDayItem
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelSmall
import com.example.workouttracker.ui.theme.labelSmallRed
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.AssignedTRPlanViewModel

/** Assigned training plan dialog to allow the user view assigned training plan */
@Composable
fun AssignedTRPlanDialog(model: TrainingPlanModel, vm: AssignedTRPlanViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData(model)
    }

    val selectedTrainingPlan by vm.selectedTrainingPlan.collectAsStateWithLifecycle()
    val selectedStartDate by vm.startDate.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {

        if (selectedTrainingPlan.description.isNotEmpty()) {
            Label(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PaddingSmall, end = PaddingSmall, bottom = PaddingSmall),
                text = selectedTrainingPlan.description,
                textAlign = TextAlign.Left,
                maxLines = 5
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = PaddingVerySmall)
        )

        if (selectedTrainingPlan.scheduledStartDate != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingVerySmall)
                    .clickable(
                        enabled = true,
                        onClick = { vm.showDatePicker() }
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    var startOnLabelStyle = labelSmall

                    if (Utils.getDateDifferenceInDays(selectedStartDate, selectedTrainingPlan.scheduledStartDate!!) != 0L) {
                        startOnLabelStyle = labelSmallRed
                    }

                    Label(
                        text = String.format(
                            stringResource(id = R.string.scheduled_start),
                            Utils.defaultFormatDate(selectedTrainingPlan.scheduledStartDate!!)
                        ),
                        style = labelSmall
                    )

                    Label(
                        text = String.format(
                            stringResource(id = R.string.start_on),
                            Utils.defaultFormatDate(selectedStartDate)
                        ),
                        style = startOnLabelStyle
                    )
                }

                ImageButton(
                    modifier = Modifier.size(30.dp),
                    onClick = { vm.showDatePicker() },
                    image = Icons.Default.DateRange
                )
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = PaddingVerySmall)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .padding(start = PaddingSmall, end = PaddingSmall, top = PaddingSmall),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PaddingSmall)
        ) {

            itemsIndexed(selectedTrainingPlan.trainingDays) { index, item ->
                TrainingDayItem(
                    trainingDay = item,
                    trainingDayIndex = index,
                    onEditClick = { _, _ -> },
                    showEdit = false
                )
            }
        }

        Row(modifier = Modifier
            .height(DialogFooterSize)
            .fillMaxWidth()
            .padding(top = PaddingMedium),
            horizontalArrangement = Arrangement.Center
        ) {

            DialogButton(
                modifier = Modifier
                    .customBorder()
                    .weight(1f),
                text = stringResource(R.string.start_btn),
                onClick = { vm.start() }
            )
        }

    }
}

@Preview
@Composable
private fun AssignedTRPlanDialogPreview() {
    WorkoutTrackerTheme {
        AssignedTRPlanDialog(TrainingPlanModel("The best training plan"))
    }
}