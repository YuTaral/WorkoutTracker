package com.example.workouttracker.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.components.dialogs.AddEditWorkoutDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    /** Display the edit workout dialog */
    @SuppressLint("StateFlowValueCalledInComposition")
    fun showEditWorkoutDialog() {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.edit_workout_title),
                content = { AddEditWorkoutDialog(workout = workoutRepository.selectedWorkout.value!!) }
            )
        }
    }
}