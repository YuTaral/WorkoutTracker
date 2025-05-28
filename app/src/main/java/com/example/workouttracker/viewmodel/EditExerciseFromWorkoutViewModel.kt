package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.ExerciseModel
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.data.models.SetModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.repositories.ExerciseRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.dialogs.MGExerciseDescDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** View model to control the UI state of Edit exercise from workout dialog */
@HiltViewModel
class EditExerciseFromWorkoutViewModel @Inject constructor(
    private var exerciseRepository: ExerciseRepository,
    private var userRepository: UserRepository,
    private var workoutRepository: WorkoutRepository
): ViewModel() {

    /** The state of each set row */
    data class SetUiState(
        val completed: Boolean = false,
        val reps: String = "",
        val weight: String = "",
        val rest: String = "",
        val id: Long = 0
    )

    /** Class containing all fields in the UI */
    data class UIState(
        val notes: String = "",
        val setsState: MutableList<SetUiState> = mutableListOf(),
        val deleteMode: Boolean = false
    )

    /** The UI state of the dialog */
    private var _uiState = MutableStateFlow(UIState())
    var uiState = _uiState.asStateFlow()

    /** The exercise to edit */
    private lateinit var editExercise: ExerciseModel

    /**
     * Initialize the state with the exercise data
     * @param exercise the exercise to edit
     */
    fun initializeState(exercise: ExerciseModel) {
        editExercise = exercise

        val sets = mutableListOf<SetUiState>()

        for (set: SetModel in exercise.sets) {
            sets.add(SetUiState(
                completed = set.completed,
                reps = if (set.reps > 0) set.reps.toString() else "",
                weight = if (set.weight > 0.0) Utils.formatDouble(set.weight) else "",
                rest = if (set.rest > 0) set.rest.toString() else "",
                id = set.id
            ))
        }

        _uiState.update {
            it.copy(
                setsState = sets,
                deleteMode = false
            )
        }
    }

    /** Update the notes in the UI with the provided value */
    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    /** Update the deletable value in the UI with the provided value */
    fun updateDeletable(value: Boolean) {
        _uiState.update {
            it.copy(deleteMode = value)
        }
    }

    /** Update the completed value of the set at the specified index */
    fun updateCompleted(index: Int, value: Boolean) {
        _uiState.update {
            if (index !in it.setsState.indices) {
                return
            }

            val updatedSets = it.setsState.toMutableList()
            updatedSets[index] = updatedSets[index].copy(completed = value)
            it.copy(setsState = updatedSets)
        }
    }

    /** Update the reps of the set at the specified index */
    fun updateReps(index: Int, value: String) {
        _uiState.update {
            if (index !in it.setsState.indices) {
                return
            }

            val updatedSets = it.setsState.toMutableList()
            updatedSets[index] = updatedSets[index].copy(reps = value)
            it.copy(setsState = updatedSets)
        }
    }

    /** Update the weight of the set at the specified index */
    fun updateWeight(index: Int, value: String) {
        _uiState.update {
            if (index !in it.setsState.indices) {
                return
            }

            val updatedSets = it.setsState.toMutableList()
            updatedSets[index] = updatedSets[index].copy(weight = value)
            it.copy(setsState = updatedSets)
        }
    }

    /** Update the rest of the set at the specified index */
    fun updateRest(index: Int, value: String) {
        _uiState.update {
            if (index !in it.setsState.indices) {
                return
            }

            val updatedSets = it.setsState.toMutableList()
            updatedSets[index] = updatedSets[index].copy(rest = value)
            it.copy(setsState = updatedSets)
        }
    }

    /** Add new set to the UI*/
    fun addSet() {
        val defaultValues = userRepository.user.value!!.defaultValues

        _uiState.update {
            val newSet = SetUiState(reps = defaultValues.reps.toString(), weight = Utils.formatDouble(defaultValues.weight),
                                    rest = defaultValues.rest.toString(), id = 0)

            val updatedSets = it.setsState.toMutableList()
            updatedSets.add(newSet)

            it.copy(setsState = updatedSets)
        }
    }

    /** Remove the set at the specified index */
    fun removeSet(index: Int) {
        _uiState.update {
            if (index !in it.setsState.indices) {
                return
            }

            val updatedSets = it.setsState.toMutableList()
            updatedSets.removeAt(index)
            it.copy(setsState = updatedSets)
        }
    }

    /** Save the changes to the exercise */
    fun save() {
        val exercise = ExerciseModel(idVal = editExercise.id, nameVal = editExercise.name,
            muscleGroupVal = MuscleGroupModel(), setsVal = getSetsData(), mGExerciseIdVal = editExercise.mGExerciseId,
            notesVal = uiState.value.notes)

        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.updateExerciseFromWorkout(
                exercise = exercise,
                workoutId = workoutRepository.selectedWorkout.value!!.id,
                onSuccess = { onChangeSuccess(it) }
            )
        }
    }

    /** Delete the exercise from workout */
    fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.deleteExerciseFromWorkout(
                exerciseId = editExercise.id,
                onSuccess = { onChangeSuccess(it) }
            )
        }
    }

    /** Show the exercise description as separate dialog */
    fun showDescription() {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.getMGExercise(
                mGExerciseId = editExercise.mGExerciseId!!,
                onSuccess = {
                    viewModelScope.launch {
                        DialogManager.showDialog(
                            title = editExercise.name,
                            dialogName = "MGExerciseDescDialog",
                            content = { MGExerciseDescDialog(it) }
                        )
                    }
                }
            )
        }

    }

    /**
     * Execute the logic to update the app after save/delete has been successful
     * @param updatedWorkout the updated workout
     */
    private fun onChangeSuccess(updatedWorkout: WorkoutModel) {
        workoutRepository.updateSelectedWorkout(updatedWorkout)

        viewModelScope.launch(Dispatchers.IO) {
            workoutRepository.updateWorkouts(null)
        }

        viewModelScope.launch {
            DialogManager.hideDialog("EditExerciseFromWorkoutDialog")
        }
    }

    /** Return the updated sets based on the UI State*/
    private fun getSetsData(): MutableList<SetModel> {
        var sets = mutableListOf<SetModel>()
        var reps = 0
        var weight = 0.0
        var rest = 0

        uiState.value.setsState.forEach {
            reps = if (it.reps.isNotEmpty()) {
                it.reps.toInt()
            } else {
                0
            }

            weight = if (it.weight.isNotEmpty()) {
                it.weight.toDouble()
            } else {
                0.0
            }

            rest = if (it.rest.isNotEmpty()) {
                it.rest.toInt()
            } else {
                0
            }

            sets.add(SetModel(idVal = it.id, repsVal = reps, weightVal = weight, restVal = rest,
                completedVal = it.completed, deletableVal = false)
            )
        }

        return sets
    }
}