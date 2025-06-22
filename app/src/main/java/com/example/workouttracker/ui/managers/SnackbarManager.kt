package com.example.workouttracker.ui.managers

import android.support.annotation.StringRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Snackbar event class */
data class SnackbarEvent(
    @StringRes val messageId: Int = 0,
    val message: String = "",
)

/** Class to handle snackbar to show message to the user */
@Singleton
class SnackbarManager @Inject constructor() {
    private val _events = MutableSharedFlow<SnackbarEvent>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /** Show snackbar snackbar with by providing message id */
    suspend fun showSnackbar(messageId: Int) {
        _events.emit(SnackbarEvent(messageId = messageId))
    }

    /** SShow snackbar snackbar with by providing message */
    suspend fun showSnackbar(message: String) {
        _events.emit(SnackbarEvent(messageId = 0, message = message))
    }
}