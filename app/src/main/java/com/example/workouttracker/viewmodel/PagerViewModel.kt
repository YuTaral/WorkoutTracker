package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.workouttracker.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** The indices of each panel - used because the temporary panels are displayed on equal index */
enum class PanelIndices {
    WORKOUTS,
    SELECTED_WORKOUT
}

/** Class representing a page */
sealed class Page(val title: Int, val icon: Int, val index: Int) {
    data object Workouts : Page(R.string.workouts_panel_title, R.drawable.icon_tab_workouts, PanelIndices.WORKOUTS.ordinal)
    data object SelectedWorkout : Page(R.string.workout_panel_title, R.drawable.icon_tab_selected_workout, PanelIndices.SELECTED_WORKOUT.ordinal)
}

/** PagerViewModel to manage the state of the pages of the main screen */
@HiltViewModel
class PagerViewModel @Inject constructor(): ViewModel() {
    private val _pages: MutableStateFlow<List<Page>> = MutableStateFlow(initializePages())
    val pages = _pages.asStateFlow()

    private var _selectedPage = MutableStateFlow<Page>(Page.Workouts)
    var selectedPage = _selectedPage.asStateFlow()

    /** Initialize the initial pages of the pager - Main and Selected Workout */
    private fun initializePages(): List<Page> {
        return listOf(
            Page.Workouts,
            Page.SelectedWorkout
        )
    }

    /**
     * Change the selected page
     * @param page the newly selected page
     */
    fun changeSelection(page: Page) {
        _selectedPage.value = page
    }
}