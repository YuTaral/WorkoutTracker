package com.example.workouttracker.ui.managers

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/** Class to handle showing loading dialog when needed (e.g request in progress) */
object LoadingManager {
    private val _events = Channel<Boolean>()
    val events = _events.receiveAsFlow()

    /** Send event to show loading dialog */
    suspend fun showLoading() {
        _events.send(true)
    }

    suspend fun hideLoading() {
        _events.send( false)
    }
}