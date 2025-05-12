package com.example.workouttracker.ui.managers
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Show dialog event class */
data class DisplayDialogEvent(
    var dialogName: String = "",
    var title: String = "",
    var content: @Composable () -> Unit = {}
)

/** Dialog action */
sealed class DialogAction {
    data class Show(val event: DisplayDialogEvent) : DialogAction()
    data class Dismiss(val dialogName: String) : DialogAction()
}

/** Class to handle showing dialogs of different type */
object DialogManager {
    private val _events = MutableSharedFlow<DialogAction>(replay = 0, extraBufferCapacity = 3)
    val events = _events.asSharedFlow()

    /**
     * Send event to display picker dialog
     * @param title the dialog title
     * @param dialogName the dialog name
     * @param content the dialog content
     */
    suspend fun showDialog(title: String = "", dialogName: String, content: @Composable () -> Unit = {}) {
        _events.emit(DialogAction.Show(DisplayDialogEvent(title = title, dialogName = dialogName, content = content)))
    }

    /** Send event to hide the dialog */
    suspend fun hideDialog(dialogName: String) {
        _events.emit(DialogAction.Dismiss(dialogName))
    }
}