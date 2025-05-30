package com.example.workouttracker.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A helper class to perform debounced search operations.
 * This class helps reduce the number of times a search/filter operation is triggered
 * by adding a delay (debounce time) before executing the provided function.
 * @param coroutineScope The scope in which the debounced search job runs (e.g., ViewModelScope).
 * @param debounceTime The delay in milliseconds to wait before triggering the search (default is 500ms).
 * @param callback A suspend function that performs the actual debounced work (e.g. filtering list).
 */
class SearchHelper(
    private val coroutineScope: CoroutineScope,
    private val debounceTime: Long = 500L,
    private val callback: suspend (String) -> Unit
) {
    /** The search field */
    private var _search = MutableStateFlow<String>("")
    var search = _search.asStateFlow()

    private var searchJob: Job? = null

    /** Update the search term and execute the callback, waiting for the specified amount of time */
    fun updateSearchTerm(value: String) {
        _search.value = value

        searchJob?.cancel()

        searchJob = coroutineScope.launch(Dispatchers.Default) {
            delay(debounceTime)
            callback(_search.value)
        }
    }
}