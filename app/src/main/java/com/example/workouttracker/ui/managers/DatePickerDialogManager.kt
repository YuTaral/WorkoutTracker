package com.example.workouttracker.ui.managers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/** Show date picker event class */
data class DisplayDatePickerEvent(
    var show: Boolean = false,
    val onCancel: () -> Unit = {},
    val onDatePick: (Date) -> Unit = {}
)

/** Class to handle showing date picker dialog */
@Singleton
class DatePickerDialogManager @Inject constructor() {

    /** Shared flow to emit events */
    private val _events = MutableSharedFlow<DisplayDatePickerEvent>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /**
     * Send event to show date picker dialog
     * @param onDatePick the callback to execute when date has been selected
     */
    suspend fun showDialog(onCancel: () -> Unit, onDatePick: (Date) -> Unit) {
        _events.emit(DisplayDatePickerEvent(
            show = true,
            onCancel = onCancel,
            onDatePick = onDatePick)
        )
    }

    /** Send event to hide date picker dialog */
    suspend fun hideDialog() {
        _events.emit(DisplayDatePickerEvent(show = false))
    }
}