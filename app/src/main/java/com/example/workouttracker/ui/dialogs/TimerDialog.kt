package com.example.workouttracker.ui.dialogs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.workouttracker.R
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.managers.CustomNotificationManager
import com.example.workouttracker.ui.managers.VibrationEvent
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.utils.Constants.TIMER_END_VIBRATION
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Timer dialog to show timer
 * @param seconds the seconds to start from
 * @param autoStart whether to auto start the timer
 * @param onDone callback to execute on done button click
 */
@SuppressLint("DefaultLocale")
@Composable
fun TimerDialog(seconds: Long, autoStart: Boolean, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isAppInBackground by rememberSaveable { mutableStateOf(false) }
    var timeLeft by rememberSaveable { mutableLongStateOf(seconds) }
    var running by rememberSaveable { mutableStateOf(autoStart) }
    val progress = (timeLeft / seconds.toFloat())
    var timeFinished by rememberSaveable { mutableStateOf(timeLeft == 0L) }
    val h = timeLeft / 3600
    val m = (timeLeft % 3600) / 60
    val s = timeLeft % 60

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isAppInBackground = when (event) {
                Lifecycle.Event.ON_STOP -> true
                Lifecycle.Event.ON_START -> false
                else -> isAppInBackground
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(running) {
        while (running) {
            if (timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1
            } else {
                timeFinished = true
                running = false

                if (isAppInBackground) {
                    CustomNotificationManager.sendNotification(
                        context = context,
                        titleId = R.string.time_is_up_lbl,
                        messageId = R.string.time_finished_lbl
                    )
                } else {
                    scope.launch {
                        VibrationManager.makeVibration(
                            event = VibrationEvent(pattern = TIMER_END_VIBRATION)
                        )
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .padding(PaddingSmall),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .size(350.dp)
                    .graphicsLayer(scaleX = -1f),
                color = ColorWhite,
                strokeWidth = 20.dp,
                trackColor = ColorAccent,
                strokeCap = StrokeCap.Butt
            )
            Label(
                text = if (timeFinished) stringResource(id = R.string.time_is_up_lbl)
                        else String.format("%02d:%02d:%02d", h, m, s),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(modifier = Modifier
            .padding(top = PaddingLarge)
            .fillMaxWidth()
            .height(DialogFooterSize)
        ) {
            DialogButton(
                modifier = Modifier
                    .customBorder(end = true)
                    .weight(1f),
                text = if (timeFinished) stringResource(R.string.restart_btn)
                        else if (running) stringResource(R.string.pause_btn)
                        else stringResource(R.string.start_btn),
                onClick = {
                    if (timeFinished) {
                        timeFinished = false
                        timeLeft = seconds
                        running = true
                    } else {
                        running = !running
                    }
                }
            )

            DialogButton(
                modifier = Modifier
                    .customBorder()
                    .weight(1f),
                text = stringResource(R.string.done_btn),
                onClick = {
                    onDone()
                }
            )
        }
    }
}

@Preview
@Composable
private fun TimerDialogPreview() {
    WorkoutTrackerTheme {
        TimerDialog(160, false, {})
    }
}