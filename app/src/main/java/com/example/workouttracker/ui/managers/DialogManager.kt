package com.example.workouttracker.ui.managers
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Show dialog event class */
data class DisplayDialogEvent(
    var show: Boolean = false,
    var title: String = "",
    var content: @Composable () -> Unit = {}
)

@Suppress("UNCHECKED_CAST")
val DisplayDialogEventSaver = Saver<DisplayDialogEvent, List<Any>>(
    save = { event ->
        listOf(event.show, event.title, event.content)
    },
    restore = { savedState ->
        DisplayDialogEvent(
            show = savedState[0] as Boolean,
            title = savedState[1] as String,
            content = savedState[2] as () -> Unit,
        )
    }
)

/** Class to handle showing dialogs of different type */
object DialogManager {
    private val _events = MutableSharedFlow<DisplayDialogEvent>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /**
     * Send event to display picker dialog
     * @param title the dialog title
     * @param content the dialog content
     */
    suspend fun showDialog(title: String = "", content: @Composable () -> Unit = {}) {
        _events.emit(DisplayDialogEvent(
            show = true,
            title = title,
            content = content)
        )
    }

    /** Send event to hide the dialog */
    suspend fun hideDialog() {
        _events.emit(DisplayDialogEvent(show = false))
    }
}