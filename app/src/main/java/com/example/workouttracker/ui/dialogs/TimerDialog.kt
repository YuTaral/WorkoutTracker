package com.example.workouttracker.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.TimerViewModel

/** Timer dialog to show timer
 * @param seconds the seconds to start from
 * @param autoStart whether to auto start the timer
 * @param onDone callback to execute on done button click
 * @param sendNotification send notification callback
 */
@SuppressLint("DefaultLocale")
@Composable
fun TimerDialog(vm: TimerViewModel = hiltViewModel(),
                seconds: Long,
                autoStart: Boolean,
                onDone: () -> Unit,
                sendNotification: (Context) -> Unit
) {
    LaunchedEffect(Unit) {
        vm.initializeData(
            seconds = seconds,
            autoStart = autoStart,
            sendNotification = sendNotification
        )
    }

    val timeLeft by vm.timeLeft.collectAsStateWithLifecycle()
    val running by vm.isRunning.collectAsStateWithLifecycle()
    val timeFinished by vm.isFinished.collectAsStateWithLifecycle()
    val isAppInBackground by vm.isAppInBackground.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val value  = when (event) {
                Lifecycle.Event.ON_STOP -> true
                Lifecycle.Event.ON_START -> false
                else -> isAppInBackground
            }
            vm.updateIsInBackground(value)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            vm.cancelJob()
        }
    }

    val h = timeLeft / 3600
    val m = (timeLeft % 3600) / 60
    val s = timeLeft % 60

    val progress = (timeLeft / seconds.toFloat())

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
                    vm.updateRunning()
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
        TimerDialog(seconds = 160, autoStart = false, onDone = {}, sendNotification = {})
    }
}