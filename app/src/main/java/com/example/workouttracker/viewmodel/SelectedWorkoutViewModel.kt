package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/** WorkoutsViewModel to manage the state of WorkoutScreen */
@HiltViewModel
class SelectedWorkoutViewModel @Inject constructor(
    var workoutRepository: WorkoutRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** Display the add workout dialog */
    fun showAddWorkoutDialog() {
        Utils.showAddWorkoutDialog(viewModelScope, resourceProvider)
    }
}