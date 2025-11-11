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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.workouttracker.R
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorDialogBackground
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.compose.runtime.derivedStateOf

/**
 * Composable to show/hide the date picker dialog
 * @param onDismiss callback to execute on dialog close
 * @param onDatePick callback to execute on date selection
 * @param allowPastDates whether to show past dates
 */
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDatePick: (Date) -> Unit,
    allowPastDates: Boolean
) {
    // Stable references & computed values
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }
    val today = remember { LocalDate.now() }

    val startMonth = if (allowPastDates) {
        YearMonth.of(2025, 1)
    } else {
        YearMonth.from(today)
    }
    // End month can be adjusted as needed; keep light by remembering the initial value
    val endMonth = remember { YearMonth.now().plusMonths(1) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

    // Derive current visible month without extra state or snapshotFlow
    val visibleYearMonth by remember(state) {
        derivedStateOf { state.firstVisibleMonth.yearMonth }
    }

    var selectedDate by remember {
        mutableStateOf(CalendarDay(LocalDate.now(), position = DayPosition.MonthDate))
    }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ColorDialogBackground,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingMedium)
                ) {
                    CurrentMonth(
                        month = visibleYearMonth,
                        onBack = {
                            scope.launch {
                                state.scrollToMonth(visibleYearMonth.minusMonths(1))
                            }
                        },
                        onForward = {
                            scope.launch {
                                state.scrollToMonth(visibleYearMonth.plusMonths(1))
                            }
                        }
                    )

                    HorizontalCalendar(
                        modifier = Modifier.fillMaxWidth(),
                        state = state,
                        dayContent = { day ->
                            val selectable = !(!allowPastDates && day.date.isBefore(today))
                            Day(
                                day = day,
                                onClick = { selectedDate = it },
                                isSelected = day == selectedDate,
                                selectable = selectable
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
                                set(
                                    selectedDate.date.year,
                                    selectedDate.date.monthValue - 1,
                                    selectedDate.date.dayOfMonth,
                                    0,
                                    0,
                                    0
                                )
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
 */
@Composable
fun CurrentMonth(month: YearMonth, onBack: () -> Unit, onForward: () -> Unit) {
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMM yyyy") }
    val formattedDate = remember(month) { month.format(monthFormatter) }

    Row(
        modifier = Modifier
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
 * @param selectable whether the allow selection of the date
 */
@Composable
fun Day(day: CalendarDay, onClick: (CalendarDay) -> Unit, isSelected: Boolean, selectable: Boolean) {
    var labelStyle =
        if (day.position == DayPosition.MonthDate) MaterialTheme.typography.labelMedium
        else labelMediumGrey

    if (!selectable) {
        // Apply only alpha while preserving other TextStyle attributes
        labelStyle = labelStyle.copy(
            color = labelStyle.color.copy(alpha = 0.38f)
        )
    }

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
                enabled = selectable,
                onClick = { if (selectable) onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Label(
            text = day.date.dayOfMonth.toString(),
            style = labelStyle
        )
    }
}

/**
 * Composable to display days of the week
 * @param daysOfWeek the days of the week
 */
@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
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
