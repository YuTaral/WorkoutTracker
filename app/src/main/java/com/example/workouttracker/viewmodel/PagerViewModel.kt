package com.example.workouttracker.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.fragments.SelectActionScreen
import com.example.workouttracker.ui.components.fragments.SelectExerciseScreen
import com.example.workouttracker.ui.components.fragments.SelectedWorkoutScreen
import com.example.workouttracker.ui.components.fragments.WorkoutsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** The indices of each screen - used because the temporary screens are displayed on equal index */
enum class PageIndices {
    WORKOUTS,
    SELECTED_WORKOUT,
    FIRST_TEMPORARY
}

/** Class representing a screen in the main pager */
sealed class Page(val title: Int, val icon: Int, val index: Int, val content: @Composable () -> Unit ) {
    data object Workouts : Page(R.string.workouts_screen_title, R.drawable.icon_screen_workouts,
                           PageIndices.WORKOUTS.ordinal, content = { WorkoutsScreen() })

    data object SelectedWorkout : Page(R.string.workout_screen_title, R.drawable.icon_screen_selected_workout,
                                  PageIndices.SELECTED_WORKOUT.ordinal, content = { SelectedWorkoutScreen() })

    data object SelectExercise : Page(R.string.select_exercise_title, R.drawable.icon_screen_add_exercise,
                                 PageIndices.FIRST_TEMPORARY.ordinal, content = { SelectExerciseScreen(manageExercises = false) })

    data object Actions : Page(R.string.select_action_title, R.drawable.icon_screen_menu,
                          PageIndices.FIRST_TEMPORARY.ordinal, content = { SelectActionScreen() })

    data object ManageExercise : Page(R.string.select_exercise_title, R.drawable.icon_screen_manage_exercise,
                                 PageIndices.FIRST_TEMPORARY.ordinal, content = { SelectExerciseScreen(manageExercises = true) })

    data object ManageTemplates : Page(R.string.templates_lbl, R.drawable.icon_screen_manage_templates,
                                  PageIndices.FIRST_TEMPORARY.ordinal, content = {  })
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
        if (page.index == PageIndices.FIRST_TEMPORARY.ordinal &&
            _pages.value.size > PageIndices.FIRST_TEMPORARY.ordinal) {
            // Replace the temporary panel if the current one is temporary
            removeTemporaryPage()
        }

        // Add the page if it's temporary
        if (!_pages.value.contains(page)) {
            _pages.value = _pages.value + page
        }

        _selectedPage.value = page

        if (_pages.value.size > PageIndices.FIRST_TEMPORARY.ordinal &&
            _selectedPage.value.index < PageIndices.FIRST_TEMPORARY.ordinal) {
            // Remove the temporary page if we switch to non temporary page
            removeTemporaryPage()
        }
    }

    /** Remove the temporary page */
    private fun removeTemporaryPage() {
        _pages.value = _pages.value.filter { it.index < PageIndices.FIRST_TEMPORARY.ordinal }
    }
}