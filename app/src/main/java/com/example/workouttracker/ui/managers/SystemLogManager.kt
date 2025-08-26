package com.example.workouttracker.ui.managers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Class to fire event and store exception message and stacktrace when exception occurs */
@Singleton
class SystemLogManager @Inject constructor() {

    /** Flow to emit events to store the exception */
    private val _events = MutableSharedFlow<Exception>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /** Emit event with the specific exception */
    suspend fun emitLogExceptionEvent(exception: Exception) {
        _events.emit(exception)
    }
}