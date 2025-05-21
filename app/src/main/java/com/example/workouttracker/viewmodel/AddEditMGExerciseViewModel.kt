package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.data.network.repositories.ExerciseRepository
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditMGExerciseUiState(
    val name: String = "",
    val notes: String = "",
    val nameError: String? = null,
    val showAddExToWorkout: Boolean? = true
)

/** View model to control the UI state of Add / Edit muscle group exercise dialog */
@HiltViewModel
class AddEditMGExerciseViewModel @Inject constructor(
    private var workoutsRepository: WorkoutRepository,
    private var exerciseRepository: ExerciseRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** Dialog state */
    private val _uiState = MutableStateFlow(AddEditMGExerciseUiState())
    val uiState = _uiState.asStateFlow()

    /** The selected muscle group id */
    private var selectedMuscleGroupId = 0L

    /** The selected muscle group exercise id */
    private var selectedMGExerciseId = 0L

    /** Manage exercise active */
    private var manageExerciseActive = false

    /** Callback to execute when add/update exercise is successful */
    private lateinit var onSaveExercise: (Boolean, List<String>) -> Unit

    /**
     * Update the UI sate to empty upon dialog recreation, as the view model is
     * HiltViewModel and is created only once per activity lifetime
     * @param mGExercise the selected muscle group exercise if any, null otherwise
     * @param muscleGroupId the selected muscle group id
     * @param manageExerciseActiveVal whether the dialog is opened when managing exercises
     * @param onSaveCallback callback to execute when add exercise is successful
     */
    fun initialize(
        mGExercise: MGExerciseModel?,
        muscleGroupId: Long,
        manageExerciseActiveVal: Boolean,
        onSaveCallback: (Boolean, List<String>) -> Unit
    ) {
        manageExerciseActive = manageExerciseActiveVal

        if (mGExercise == null) {
            updateName("")
            updateNotes("")
            selectedMuscleGroupId = muscleGroupId

            if (workoutsRepository.selectedWorkout.value == null) {
                updateAddExerciseToWorkout(null)
            } else {
                updateAddExerciseToWorkout(!manageExerciseActive)
            }
        } else {
            updateName(mGExercise.name)
            updateNotes(mGExercise.description)
            selectedMuscleGroupId = mGExercise.muscleGroupId
            selectedMGExerciseId = mGExercise.id
            updateAddExerciseToWorkout(null)
        }

        onSaveExercise = onSaveCallback
    }

    /** Update the name in the UI with the provided value */
    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    /** Update the notes in the UI with the provided value */
    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    /** Update the name error in the UI with the provided value */
    fun updateNameError(value: String?) {
        _uiState.update { it.copy(nameError = value) }
    }

    /** Update the add exercise to workout checkbox state in the UI with the provided value */
    fun updateAddExerciseToWorkout(value: Boolean?) {
        _uiState.update { it.copy(showAddExToWorkout = value) }
    }

    /**
     * Add/edit the exercise if it's valid
     * @param add true to add the exercise, false to edit
     */
    fun saveExercise(add: Boolean) {
        if (!validate()) {
            return
        }

        if (add) {
            addExercise()
        } else {
            editExercise()
        }
    }

    /** Send request to add the exercise */
    private fun addExercise() {
        val addExToWorkout = _uiState.value.showAddExToWorkout != null && _uiState.value.showAddExToWorkout!!
        val workoutId = if (addExToWorkout) {
            workoutsRepository.selectedWorkout.value!!.id
        } else {
            0
        }

        val exercise = MGExerciseModel(
            idVal = 0L,
            nameVal = _uiState.value.name,
            descriptionVal = _uiState.value.notes,
            muscleGroupIdVal = selectedMuscleGroupId
        )

        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.addExercise(
                exercise = exercise,
                workoutId = workoutId,
                onlyForUser = if (manageExerciseActive) "Y" else "N",
                checkExistingEx = "N",
                onSuccess = { onSaveExercise(addExToWorkout, it) },
                onFailure = {}
            )
        }
    }

    /** Send request to edit the exercise */
    private fun editExercise() {
        val exercise = MGExerciseModel(
            idVal = selectedMGExerciseId,
            nameVal = _uiState.value.name,
            descriptionVal = _uiState.value.notes,
            muscleGroupIdVal = selectedMuscleGroupId
        )

        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.updateExercise(
                exercise = exercise,
                onlyForUser = "Y",
                onSuccess = { onSaveExercise(false, it) },
            )
        }
    }

    /** Validate the fields in the UI, return true if valid, false otherwise */
    private fun validate(): Boolean {
        if (_uiState.value.name.isEmpty()) {
            viewModelScope.launch { VibrationManager.makeVibration() }
            updateNameError(resourceProvider.getString(R.string.error_msg_enter_exercise_name))
            return false
        } else {
            updateNameError(null)
        }
        return true
    }
}