package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.extensions.customBorder
import com.example.workouttracker.ui.components.reusable.DialogButton
import com.example.workouttracker.ui.components.reusable.ImageButton
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorDialogBackground
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Composable to show/hide the date picker dialog
 * @param onDismiss callback to execute on dialog close
 * @param onDatePick callback to execute on date selection
 */
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDatePick: (Date) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }
    val state = rememberCalendarState(
        startMonth = YearMonth.of(2024, 6),
        endMonth = YearMonth.now().plusMonths(1),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )
    var selectedDate by remember {
        mutableStateOf(CalendarDay(LocalDate.now(), position = DayPosition.MonthDate))
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleMonth }
            .collect { visibleMonth ->
                currentMonth = visibleMonth.yearMonth
            }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ColorDialogBackground,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingMedium)
                ) {
                    CurrentMonth(
                        month = currentMonth,
                        onBack = {
                            scope.launch {
                                state.scrollToMonth(currentMonth.minusMonths(1))
                            }
                        },
                        onForward = {
                            scope.launch {
                                state.scrollToMonth(currentMonth.plusMonths(1))
                            }
                        }
                    )
                    HorizontalCalendar(
                        modifier = Modifier.fillMaxWidth(),
                        state = state,
                        dayContent = { Day(
                                day = it,
                                onClick = {
                                     selectedDate = it
                                },
                                isSelected = it == selectedDate
                            )
                         },
                        monthHeader = {
                            DaysOfWeekTitle(daysOfWeek)
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DialogFooterSize)
                        .background(ColorDialogBackground)
                ) {
                    DialogButton(
                        modifier = Modifier
                            .weight(1f)
                            .customBorder(end = true),
                        text = stringResource(id = R.string.cancel_btn),
                        onClick = onDismiss
                    )
                    DialogButton(
                        modifier = Modifier
                            .weight(1f)
                            .customBorder(),
                        text = stringResource(id = R.string.select_date_btn),
                        onClick = {
                            val cal = Calendar.getInstance().apply {
                                set(selectedDate.date.year, selectedDate.date.monthValue - 1, selectedDate.date.dayOfMonth, 0, 0, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            onDatePick(cal.time)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Composable to display the selected date
 * @param month the selected day
 * */
@Composable
fun CurrentMonth(month: YearMonth, onBack: () -> Unit, onForward: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
    val formattedDate = month.format(formatter)

    Row(modifier = Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageButton(
            onClick = { onBack() },
            image = Icons.AutoMirrored.Filled.ArrowBack,
            size = SmallImageButtonSize,
            buttonColor = Color.Transparent,
            imageColor = ColorAccent
        )

        Label(
            modifier = Modifier.weight(1f),
            text = formattedDate,
            style = MaterialTheme.typography.titleMedium
        )

        ImageButton(
            onClick = { onForward() },
            image = Icons.AutoMirrored.Filled.ArrowForward,
            size = SmallImageButtonSize,
            buttonColor = Color.Transparent,
            imageColor = ColorAccent
        )
    }
}

/**
 * Composable to each day
 * @param day the day
 * @param onClick callback to select the date
 * @param isSelected whether that's the currently selected date
 */
@Composable
fun Day(day: CalendarDay, onClick: (CalendarDay) -> Unit, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .padding(PaddingVerySmall)
            .aspectRatio(1f)
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = ColorAccent,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                enabled = true,
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Label(
            text = day.date.dayOfMonth.toString(),
            style = if (day.position == DayPosition.MonthDate) MaterialTheme.typography.labelMedium
                    else labelMediumGrey
        )
    }
}

/**
 * Composable to display days of the week
 * @param daysOfWeek the days of the week
 */
@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = PaddingSmall)
    ) {
        for (dayOfWeek in daysOfWeek) {
            Label(
                modifier = Modifier.weight(1f),
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(bottom = PaddingSmall),
        color = ColorBorder,
        thickness = 1.dp
    )
}
