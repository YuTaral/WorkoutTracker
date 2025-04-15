package com.example.workouttracker.ui.managers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Class to handle showing loading dialog when needed (e.g request in progress) */
object LoadingManager {
    private val _events = MutableSharedFlow<Boolean>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /** Send event to show loading dialog */
    suspend fun showLoading() {
        _events.emit(true)
    }

    suspend fun hideLoading() {
        _events.emit( false)
    }
}