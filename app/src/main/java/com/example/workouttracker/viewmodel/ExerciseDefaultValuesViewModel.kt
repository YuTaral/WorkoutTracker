package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.network.repositories.UserProfileRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.example.workouttracker.data.models.UserDefaultValuesModel
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.ui.managers.DialogManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class ExDefaultValuesUiState(
    val sets: String = "",
    val reps: String = "",
    val weight: String = "",
    val rest: String = "",
    val weightUnit: String = "",
    val completed: Boolean = false,
    val disableWeightUnit: Boolean = true
)

/** View model to control the UI state of Exercise Default values dialog */
@HiltViewModel
class ExerciseDefaultValuesViewModel @Inject constructor(
    var userRepository: UserRepository,
    private var userProfileRepository: UserProfileRepository,
    private var workoutRepository: WorkoutRepository
): ViewModel() {

    /** Dialog state */
    private val _uiState = MutableStateFlow(ExDefaultValuesUiState())
    val uiState = _uiState.asStateFlow()

    /** Tracks whether the default values are for specific exercise */
    private var mgExerciseId = 0L

    /**
     * Initialize the data required to populate the dialog
     */
    fun initializeData(values: UserDefaultValuesModel?) {
        val defaultValues = values ?: userRepository.user.value!!.defaultValues
        val sets = defaultValues.sets.toString()
        val reps = defaultValues.reps.toString()
        val rest = defaultValues.rest.toString()
        val weight = Utils.formatDouble(defaultValues.weight)

        updateSets(if (sets != "0") sets else "")
        updateReps(if (reps != "0") reps else "")
        updateRest(if (rest != "0") rest else "")
        updateWeight(if (weight != "0") weight else "")
        updateWeightUnit(defaultValues.weightUnit.text)
        updateCompleted(defaultValues.completed)
        mgExerciseId = defaultValues.mGExerciseId
        updateDisableWeightUnit(mgExerciseId != 0L)
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

    /** Update the elected weight unit in the UI with the provided value */
    fun updateWeightUnit(value: String) {
        _uiState.update { it.copy(weightUnit = value) }
    }

    /** Update the completed in the UI with the provided value */
    fun updateCompleted(value: Boolean) {
        _uiState.update { it.copy(completed = value) }
    }

    /** Update the disable weight unit boolean with the provided value */
    private fun updateDisableWeightUnit(value: Boolean) {
        _uiState.update { it.copy(disableWeightUnit = value) }
    }

    /** Save the changes to the default values */
    fun save() {
        val state = _uiState.value
        var exerciseSets = if (state.sets.isNotEmpty()) state.sets.toInt() else 0
        var setReps = if (state.reps.isNotEmpty()) state.reps.toInt() else 0
        var exerciseWeight =  if (state.weight.isNotEmpty()) state.weight.toDouble() else 0.0
        var setRest = if (state.rest.isNotEmpty()) state.rest.toInt() else 0
        var weightUnit = workoutRepository.weighUnits.value.find { it.text == state.weightUnit}

        val values = UserDefaultValuesModel(idVal = userRepository.user.value!!.defaultValues.id,
            setsVal = exerciseSets, repsVal = setReps, weightVal = exerciseWeight,
            restVal = setRest, completedVal = state.completed, weightUnitVal = weightUnit!!, mGExerciseIdVal = mgExerciseId)

        viewModelScope.launch(Dispatchers.IO) {
            userProfileRepository.updateUserDefaultValues(
                values = values,
                onSuccess = {
                    if (mgExerciseId == 0L) {
                        userRepository.updateDefaultValues(it)
                    }

                    viewModelScope.launch {
                        DialogManager.hideDialog("ExerciseDefaultValuesDialog")
                    }
                }
            )
        }
    }
}