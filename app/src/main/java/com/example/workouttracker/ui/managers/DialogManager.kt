package com.example.workouttracker.ui.managers
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
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

/** Display event saver */
@Suppress("UNCHECKED_CAST")
val DisplayDialogEventSaver = Saver<DisplayDialogEvent, List<Any>>(
    save = { event ->
        listOf(event.dialogName, event.title, event.content)
    },
    restore = { savedState ->
        DisplayDialogEvent(
            dialogName = savedState[0] as String,
            title = savedState[1] as String,
            content = savedState[2] as @Composable () -> Unit,
        )
    }
)

/** Display event list saver */
val DisplayDialogEventListSaver: Saver<List<DisplayDialogEvent>, Any> =
    listSaver(
        save = { list ->
            list.map { event ->
                DisplayDialogEventSaver.run { save(event) ?: error("Failed to save") }
            }
        },
        restore = { savedList ->
            savedList.map { saved ->
                DisplayDialogEventSaver.run { restore(saved) ?: error("Failed to restore") }
            }
        }
    )

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