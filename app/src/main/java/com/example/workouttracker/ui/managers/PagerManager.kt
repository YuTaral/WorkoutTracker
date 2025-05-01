package com.example.workouttracker.ui.managers

import com.example.workouttracker.viewmodel.Page
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Pager manager used to change the selected page */
object PagerManager {
    private val _events = MutableSharedFlow<Page>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /**
     * Emit event to change the selected page
     * @param page the page to select
     */
    suspend fun changePageSelection(page: Page) {
        _events.emit(page)
    }
}