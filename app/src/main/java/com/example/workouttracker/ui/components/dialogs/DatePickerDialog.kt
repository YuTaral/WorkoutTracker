package com.example.workouttracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.extensions.customBorder
import com.example.workouttracker.ui.components.reusable.DialogButton
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorDialogBackground
import com.example.workouttracker.ui.theme.ColorGrey
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingSmall
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Composable to show/hide the date picker dialog
 * @param onDismiss callback to execute on dialog close
 * @param onDatePick callback to execute on date selection
 */
fun DatePickerDialog(onDismiss: () -> Unit, onDatePick: (Date) -> Unit) {
    val today = remember { Calendar.getInstance() }
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = Calendar.getInstance().apply { timeInMillis = utcTimeMillis }

            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            return date.timeInMillis <= today.timeInMillis
        }
    }
    val startYear = today.get(Calendar.YEAR) - 1
    val datePickerState = rememberDatePickerState(
        yearRange = IntRange(startYear, startYear + 1),
        selectableDates = selectableDates
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = ColorDialogBackground
        ) {
            Column {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier
                        .padding(horizontal = PaddingSmall)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    showModeToggle = false,
                    title = null,
                    headline = null,
                    colors = getDatePickerColors()
                )

                Row(modifier = Modifier
                        .height(DialogFooterSize)
                        .fillMaxWidth()
                        .background(ColorDialogBackground),
                ) {
                    DialogButton(
                        modifier = Modifier
                            .weight(1f)
                            .customBorder(),
                        text = stringResource(id = R.string.cancel_btn),
                        onClick = onDismiss
                    )
                    DialogButton(
                        modifier = Modifier
                            .weight(1f)
                            .customBorder(),
                        text = stringResource(id = R.string.select_date_btn),
                        onClick = {
                            val selected = datePickerState.selectedDateMillis
                            onDatePick(selected?.let { Date(it) } ?: Date())
                        }
                    )
                }
            }
        }
    }
}

/** Return date picker colors class */
@OptIn(ExperimentalMaterial3Api::class)
private fun getDatePickerColors(): DatePickerColors {
    return DatePickerColors(
        containerColor = ColorDialogBackground,
        titleContentColor = ColorWhite,
        headlineContentColor = ColorWhite,
        weekdayContentColor = ColorWhite,
        subheadContentColor = ColorWhite,
        navigationContentColor = ColorWhite,
        yearContentColor = ColorWhite,
        disabledYearContentColor = ColorGrey,
        currentYearContentColor = ColorWhite,
        selectedYearContentColor = ColorWhite,
        disabledSelectedYearContentColor = ColorGrey,
        selectedYearContainerColor = ColorAccent,
        disabledSelectedYearContainerColor = ColorGrey,
        dayContentColor = ColorWhite,
        disabledDayContentColor = ColorGrey,
        selectedDayContentColor = ColorWhite,
        disabledSelectedDayContentColor = ColorGrey,
        disabledSelectedDayContainerColor = ColorGrey,
        todayContentColor = ColorWhite,
        todayDateBorderColor = ColorAccent,
        dayInSelectionRangeContainerColor = ColorWhite,
        dayInSelectionRangeContentColor = ColorDialogBackground,
        dividerColor = ColorBorder,
        selectedDayContainerColor = ColorAccent,
        dateTextFieldColors = TextFieldColors(
            focusedTextColor = ColorWhite,
            unfocusedTextColor = ColorWhite,
            disabledTextColor = ColorGrey,
            errorTextColor = ColorWhite,
            focusedContainerColor = ColorWhite,
            unfocusedContainerColor = ColorWhite,
            disabledContainerColor = ColorWhite,
            errorContainerColor = ColorWhite,
            cursorColor = ColorWhite,
            errorCursorColor = ColorWhite,
            textSelectionColors= TextSelectionColors(
                handleColor = ColorWhite,
                backgroundColor = ColorWhite
            ),
            focusedIndicatorColor = ColorWhite,
            unfocusedIndicatorColor = ColorWhite,
            disabledIndicatorColor = ColorWhite,
            errorIndicatorColor = ColorWhite,
            focusedLeadingIconColor = ColorWhite,
            unfocusedLeadingIconColor = ColorWhite,
            disabledLeadingIconColor = ColorWhite,
            errorLeadingIconColor = ColorWhite,
            focusedTrailingIconColor = ColorWhite,
            unfocusedTrailingIconColor = ColorWhite,
            disabledTrailingIconColor = ColorWhite,
            errorTrailingIconColor = ColorWhite,
            focusedLabelColor = ColorWhite,
            unfocusedLabelColor = ColorWhite,
            disabledLabelColor = ColorWhite,
            errorLabelColor = ColorWhite,
            focusedPlaceholderColor = ColorWhite,
            unfocusedPlaceholderColor = ColorWhite,
            disabledPlaceholderColor = ColorWhite,
            errorPlaceholderColor = ColorWhite,
            focusedSupportingTextColor = ColorWhite,
            unfocusedSupportingTextColor = ColorWhite,
            disabledSupportingTextColor = ColorWhite,
            errorSupportingTextColor = ColorWhite,
            focusedPrefixColor = ColorWhite,
            unfocusedPrefixColor = ColorWhite,
            disabledPrefixColor = ColorWhite,
            errorPrefixColor = ColorWhite,
            focusedSuffixColor = ColorWhite,
            unfocusedSuffixColor = ColorWhite,
            disabledSuffixColor = ColorWhite,
            errorSuffixColor = ColorWhite,
        ),
    )
}