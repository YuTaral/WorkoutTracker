package com.example.workouttracker.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import com.example.workouttracker.ui.dialogs.RequestInProgressSpinner
import com.example.workouttracker.ui.components.Navigation
import com.example.workouttracker.ui.dialogs.AskQuestionDialog
import com.example.workouttracker.ui.dialogs.BaseDialog
import com.example.workouttracker.ui.dialogs.DatePickerDialog
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DatePickerDialogManager
import com.example.workouttracker.ui.managers.DialogAction
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.managers.LoadingManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.DisplayDatePickerEvent
import com.example.workouttracker.ui.managers.DisplayDialogEvent
import com.example.workouttracker.ui.managers.SnackbarManager
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.viewmodel.MainViewModel
import kotlinx.coroutines.launch

/**
 * The application root screen containing the navigation and the logic to show snackbar, make vibrations
 * and show dialogs
 */
@Composable
fun Screen(vm: MainViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            WorkoutTrackerTheme {
                ShowLoading(loadingManager = vm.loadingManager)
                ShowSnackbar(snackbarHostState, context, snackbarManager = vm.snackbarManager)
                MakeVibration(context, vibrationManager = vm.vibrationManager)
                AskQuestion(askQuestionDialogManager = vm.askQuestionManager)
                ShowDatePicker(datePickerDialog = vm.datePickerManager)
                ShowDialog(dialogManager = vm.dialogManager)
                StoreSystemLog(vm = vm)

                Navigation(modifier = Modifier.padding(innerPadding), vm = vm)
            }
        }

        CustomSnackbarHost(snackbarHostState)
    }
}

/** Composable to display the snackbar at the top of the screen */
@Composable
private fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PaddingLarge),
        contentAlignment = Alignment.TopCenter
    ) {
        SnackbarHost(hostState = snackbarHostState)
    }
}

/** Composable to show/hide the loading dialog */
@Composable
private fun ShowLoading(loadingManager: LoadingManager) {
    var showLoading by rememberSaveable { mutableStateOf(false) }

    // Show loading
    LaunchedEffect(Unit) {
        loadingManager.events.collect { isLoading ->
            showLoading = isLoading
        }
    }

    if (showLoading) {
        RequestInProgressSpinner()
    }
}

/** Composable function to show snackbar */
@Composable
private fun ShowSnackbar(snackbarHostState: SnackbarHostState, context: Context, snackbarManager: SnackbarManager) {
    LaunchedEffect(Unit) {
        snackbarManager.events.collect { event ->
            snackbarHostState.currentSnackbarData?.dismiss()

            val message = if (event.messageId > 0) getString(context, event.messageId)
            else event.message

            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = null,
                withDismissAction = true
            )
        }
    }
}

/** Composable function to trigger vibration effect */
@Composable
private fun MakeVibration(context: Context, vibrationManager: VibrationManager) {
    // Trigger vibrations
    LaunchedEffect(Unit) {
        vibrationManager.events.collect { event ->
            vibrationManager.makeVibration(context, event)
        }
    }
}

/** Composable to show/hide ask question dialog */
@Composable
private fun AskQuestion(askQuestionDialogManager: AskQuestionDialogManager) {
    var showQuestionDialog by remember {
        mutableStateOf(DisplayAskQuestionDialogEvent(null, false))
    }
    var scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        askQuestionDialogManager.events.collect { event ->
            showQuestionDialog = event
        }
    }

    if (showQuestionDialog.show) {
        AskQuestionDialog(
            event = showQuestionDialog,
            hideQuestion = {
                scope.launch {
                    askQuestionDialogManager.hideQuestion()
                }
            }
        )
    }
}

/** Show/hide the date picker dialog */
@Composable
private fun ShowDatePicker(datePickerDialog: DatePickerDialogManager) {
    var showDatePickEvent by remember {
        mutableStateOf(DisplayDatePickerEvent(false))
    }

    LaunchedEffect(Unit) {
        datePickerDialog.events.collect { event ->
            showDatePickEvent = event
        }
    }

    if (showDatePickEvent.show) {
        DatePickerDialog(
            onDismiss = showDatePickEvent.onCancel,
            onDatePick = { date ->
                showDatePickEvent.onDatePick(date)
            },
            allowPastDates = showDatePickEvent.allowPastDates
        )
    }
}

/** Composable to show/hide dialog of different types */
@Composable
private fun ShowDialog(dialogManager: DialogManager) {
    var dialogEvents by remember {
        mutableStateOf<List<DisplayDialogEvent>>(emptyList())
    }
    var scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        dialogManager.events.collect { event ->
            dialogEvents = when (event) {
                is DialogAction.Show -> {
                    dialogEvents + event.event
                }
                is DialogAction.Dismiss -> {
                    dialogEvents.filterNot { it.dialogName == event.dialogName }
                }
            }
        }
    }

    dialogEvents.forEach { event ->
        BaseDialog(
            title = event.title,
            dialogName = event.dialogName,
            content = event.content,
            hideDialog = {
                scope.launch {
                    dialogManager.hideDialog(event.dialogName)
                }
            }
        )
    }
}

/** Composable to store system log when exception occurs */
@Composable
private fun StoreSystemLog(vm: MainViewModel) {
    LaunchedEffect(Unit) {
        vm.systemLogManager.events.collect { event ->
            vm.addSystemLog(event)
        }
    }
}

