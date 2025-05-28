package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.extensions.customBorder
import com.example.workouttracker.ui.components.reusable.DialogButton
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Dialog to select timer duration
 * @param onStart callback to execute on start button click
 */
@Composable
fun StartTimerDialog(onStart: (Long) -> Unit) {
    var selectedHour by remember { mutableIntStateOf(0) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var selectedSecond by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = PaddingSmall, end = PaddingSmall, bottom = PaddingLarge),
            horizontalArrangement = Arrangement.spacedBy(PaddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Label(
                    modifier = Modifier.padding(bottom = PaddingVerySmall),
                    text = stringResource(id = R.string.hours_lbl),
                    style = MaterialTheme.typography.titleMedium
                )
                NumberPicker(
                    value = selectedHour,
                    range = 0..23,
                    onValueChange = { selectedHour = it }
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Label(
                    modifier = Modifier.padding(bottom = PaddingVerySmall),
                    text = stringResource(id = R.string.minutes_lbl),
                    style = MaterialTheme.typography.titleMedium
                )
                NumberPicker(
                    value = selectedMinute,
                    range = 0..59,
                    onValueChange = { selectedMinute = it }
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            )  {
                Label(
                    modifier = Modifier.padding(bottom = PaddingVerySmall),
                    text = stringResource(id = R.string.seconds_lbl),
                    style = MaterialTheme.typography.titleMedium
                )
                NumberPicker(
                    value = selectedSecond,
                    range = 0..59,
                    onValueChange = { selectedSecond = it }
                )
            }
        }

        Row(modifier = Modifier
            .height(DialogFooterSize)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            DialogButton(
                modifier = Modifier
                    .customBorder()
                    .weight(1f),
                text = stringResource(R.string.start_timer_lbl),
                onClick = {
                    onStart((selectedHour * 3600 + selectedMinute * 60 + selectedSecond).toLong())
                }
            )
        }
    }
}

/**
 * Custom number picker with swipe behavior
 * @param value the initial value
 * @param range the range
 * @param onValueChange the callback to execute on change
 */
@OptIn(FlowPreview::class)
@Composable
fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val itemHeight = 60.dp
    val listHeight = itemHeight * 3
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = value - range.first)
    val centeredItemIndex by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            val indexOffset = if (offset > itemHeight.value.toInt() / 2) 1 else 0
            (listState.firstVisibleItemIndex + indexOffset).coerceIn(0, range.count() - 1)
        }
    }

    Box(modifier = Modifier.size(width = itemHeight, height = listHeight)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeight),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(range.count()) { index ->
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .clickable {
                            onValueChange(range.first + index)
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%02d", range.first + index),
                        style = if (index == centeredItemIndex) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            labelMediumGrey
                        }
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -itemHeight / 2),
            color = ColorBorder,
            thickness = 1.dp
        )

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = itemHeight / 2),
            color = ColorBorder,
            thickness = 1.dp
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val offset = listState.firstVisibleItemScrollOffset
            val indexOffset = if (offset > itemHeight.value.toInt() / 2) 1 else 0
            (listState.firstVisibleItemIndex + indexOffset).coerceIn(0, range.count() - 1)
        }
        .debounce(150)
        .distinctUntilChanged()
        .collectLatest { centeredIndex ->
            onValueChange((range.first + centeredIndex).coerceIn(range))
        }
    }
}

@Preview
@Composable
private fun StartTimerDialogPreview() {
    WorkoutTrackerTheme {
        StartTimerDialog({})
    }
}