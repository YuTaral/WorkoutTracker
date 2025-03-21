package com.example.workouttracker.ui.managers

import android.support.annotation.StringRes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/** Snackbar event class */
data class SnackbarEvent(
    @StringRes val messageId: Int = 0,
    val message: String = "",
)

/** Class to handle snackbar to show message to the user */
object SnackbarManager {
    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    /** Show snackbar snackbar with by providing message id */
    suspend fun showSnackbar(messageId: Int) {
        _events.send(SnackbarEvent(messageId = messageId))
    }

    /** SShow snackbar snackbar with by providing message */
    suspend fun showSnackbar(message: String) {
        _events.send(SnackbarEvent(messageId = 0, message = message))
    }

}