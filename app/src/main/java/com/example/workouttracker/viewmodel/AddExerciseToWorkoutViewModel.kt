package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.ExerciseModel
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.data.network.repositories.ExerciseRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** View model to control the UI state of Add exercise to workout dialog */
@HiltViewModel
class AddExerciseToWorkoutViewModel @Inject constructor(
    private var userRepository: UserRepository,
    private var exerciseRepository: ExerciseRepository,
    private var workoutRepository: WorkoutRepository,
    private val dialogManager: DialogManager,
    private val pagerManager: PagerManager
): ViewModel() {

    /** Class containing all fields in the UI */
    data class UISTate(
        val notes: String = "",
        val sets: String = "",
        val reps: String = "",
        val weight: String = "",
        val rest: String = "",
        val completed: Boolean = false
    )

    /** Dialog state */
    private val _uiState = MutableStateFlow(UISTate())
    val uiState = _uiState.asStateFlow()

    var exerciseToAdd: MGExerciseModel? = null

    /**
     * Initialize the data required to populate the dialog
     * @param mGExercise the muscle group exercise chosen by the user
     * */
    fun initializeData(mGExercise: MGExerciseModel) {
        exerciseToAdd = mGExercise
        initializeState()
    }

    /** Update the notes in the UI with the provided value */
    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    /** Update the sets in the UI with the provided value */
    fun updateSets(value: String) {
        _uiState.update { it.copy(sets = value) }
    }

    /** Update the reps in the UI with the provided value */
    fun updateReps(value: String) {
        _uiState.update { it.copy(reps = value) }
    }

    /** Update the weight in the UI with the provided value */
    fun updateWeight(value: String) {
        _uiState.update { it.copy(weight = value) }
    }

    /** Update the rest in the UI with the provided value */
    fun updateRest(value: String) {
        _uiState.update { it.copy(rest = value) }
    }

    /** Update the completed in the UI with the provided value */
    fun updateCompleted(value: Boolean) {
        _uiState.update { it.copy(completed = value) }
    }

    /** Send a request to add the exercise */
    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.addExerciseToWorkout(
                exercise = createExerciseModel(),
                workoutId = workoutRepository.selectedWorkout.value!!.id,
                onSuccess = { updatedWorkout ->
                    workoutRepository.updateSelectedWorkout(updatedWorkout)

                    viewModelScope.launch(Dispatchers.IO) {
                        workoutRepository.updateWorkouts(null, null)
                    }

                    viewModelScope.launch {
                        dialogManager.hideDialog("AddExerciseToWorkoutDialog")
                        pagerManager.changePageSelection(Page.SelectedWorkout)
                    }
                }
            )
        }
    }

    /** Set the UI state to the default values */
    private fun initializeState() {
        val defaultValues = userRepository.user.value!!.defaultValues
        val sets = defaultValues.sets.toString()
        val reps = defaultValues.reps.toString()
        val rest = defaultValues.rest.toString()
        val weight = Utils.formatDouble(defaultValues.weight)

        updateSets(if (sets != "0") sets else "")
        updateReps(if (reps != "0") reps else "")
        updateRest(if (rest != "0") rest else "")
        updateCompleted(defaultValues.completed)
        updateWeight(if (weight != "0") weight else "")
    }

    /** Validate the data in the dialog when save is clicked and exercise is being added to the workout */
    private fun createExerciseModel(): ExerciseModel {
        val state = uiState.value

        val exerciseName = exerciseToAdd!!.name.toString()
        var exerciseSets = 0
        var setReps = 0
        var setRest = 0
        var exerciseWeight = 0.0

        if (state.sets.isNotEmpty()) {
            exerciseSets = state.sets.toInt()
        }
        if (state.reps.isNotEmpty()) {
            setReps = state.reps.toInt()
        }
        if (state.weight.isNotEmpty()) {
            exerciseWeight = state.weight.toDouble()
        }

        if (state.rest.isNotEmpty()) {
            setRest = state.rest.toInt()
        }

        val model = MuscleGroupModel(exerciseToAdd!!.muscleGroupId, exerciseToAdd!!.name, "")

        return ExerciseModel(exerciseName, model, exerciseSets, setReps, exerciseWeight, setRest,
            state.completed, exerciseToAdd!!.id, state.notes)
    }
}