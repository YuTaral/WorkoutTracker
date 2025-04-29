package com.example.workouttracker.ui.managers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Date
import androidx.compose.runtime.saveable.Saver

/** Show date picker event class */
data class DatePickerEvent(
    var show: Boolean = false,
    val onCancel: () -> Unit = {},
    val onDatePick: (Date) -> Unit = {}
)

@Suppress("UNCHECKED_CAST")
val DatePickerEventSaver = Saver<DatePickerEvent, List<Any>>(
    save = { event ->
        listOf(event.show, event.onCancel, event.onDatePick)
    },
    restore = { savedState ->
        DatePickerEvent(
            show = savedState[0] as Boolean,
            onCancel = savedState[1] as () -> Unit,
            onDatePick = savedState[2] as (Date) -> Unit
        )
    }
)

/** Class to handle showing date picker dialog */
object DatePickerDialogManager {
    private val _events = MutableSharedFlow<DatePickerEvent>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /**
     * Send event to show date picker dialog
     * @param onDatePick the callback to execute when date has been selected
     */
    suspend fun showDialog(onCancel: () -> Unit, onDatePick: (Date) -> Unit) {
        _events.emit(DatePickerEvent(
            show = true,
            onCancel = onCancel,
            onDatePick = onDatePick)
        )
    }

    /** Send event to hide date picker dialog */
    suspend fun hideDialog() {
        _events.emit(DatePickerEvent(show = false))
    }
}