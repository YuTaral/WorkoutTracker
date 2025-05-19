package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.data.network.repositories.ExerciseRepository
import com.example.workouttracker.data.network.repositories.MuscleGroupRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.components.dialogs.AddExerciseToWorkoutDialog
import com.example.workouttracker.ui.managers.DialogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.example.workouttracker.R
import com.example.workouttracker.utils.ResourceProvider

/** Enum with different states of the screen */
enum class Mode {
    SELECT_MUSCLE_GROUP,
    SELECT_EXERCISE
}

/** Enum representing the actions from the action spinner */
enum class SpinnerActions(private val stringId: Int) {
    UPDATE_EXERCISE(R.string.action_update_exercise),
    DELETE_EXERCISE(R.string.action_delete_exercise),
    CHANGE_EXERCISE_DEFAULT_VALUES(R.string.action_exercise_default_values);

    fun getStringId(): Int {
        return stringId
    }
}

/** View model to control the UI state of add exercise to workout screen */
@HiltViewModel
class SelectExerciseViewModel @Inject constructor(
    var muscleGroupsRepository: MuscleGroupRepository,
    var exerciseRepository: ExerciseRepository,
    private var userRepository: UserRepository,
    private var resourceProvider: ResourceProvider
): ViewModel() {
    /** Use job with slight delay to avoid filtering the data on each letter */
    private val debounceTime = 500L
    private var searchJob: Job? = null

    /** The selected muscle group id */
    private var selectedMuscleGroupId = 0L

    /** Mode of the screen */
    private var _mode = MutableStateFlow<Mode>(Mode.SELECT_MUSCLE_GROUP)
    var mode = _mode.asStateFlow()

    /** The muscle groups */
    private var _muscleGroups: MutableList<MuscleGroupModel> = mutableListOf()
    private var _filteredMuscleGroups = MutableStateFlow<MutableList<MuscleGroupModel>>(mutableListOf())
    var filteredMuscleGroups = _filteredMuscleGroups.asStateFlow()

    /** The muscle groups exercises */
    private var _mGExercises: MutableList<MGExerciseModel> = mutableListOf()
    private var _filteredmGExercises = MutableStateFlow<MutableList<MGExerciseModel>>(mutableListOf())
    var filteredmGExercises = _filteredmGExercises.asStateFlow()

    /** The search term for muscle groups / exercises */
    private var _search = MutableStateFlow<String>("")
    var search = _search.asStateFlow()

    /** Whether the screen is in manage exercise mode */
    private var _manageExercises = MutableStateFlow<Boolean>(false)
    var manageExercises = _manageExercises.asStateFlow()

    /** Valid Spinner actions */
    var spinnerActions: List<SpinnerActions> = listOf(
        SpinnerActions.UPDATE_EXERCISE,
        SpinnerActions.DELETE_EXERCISE,
        SpinnerActions.CHANGE_EXERCISE_DEFAULT_VALUES,
    )

    /** The selected spinner action, if any */
    private var _selectedSpinnerAction = MutableStateFlow<SpinnerActions?>(null)
    var selectedSpinnerAction = _selectedSpinnerAction.asStateFlow()

    /** Update the mode with the provided value */
    fun updateMode(newMode: Mode) {
        _mode.value = newMode
        _search.value = ""

        if (_mode.value == Mode.SELECT_MUSCLE_GROUP) {
            _mGExercises.clear()
            _filteredmGExercises.value.clear()
        } else {
            populateMGExercises()
        }
    }

    init {
        populateMuscleGroups()
    }

    /** Update manage exercise value when the screen is shown */
    fun updateManageExercises(value: Boolean) {
        _manageExercises.value = value

        if (_manageExercises.value) {
            updateSelectedSpinnerAction(resourceProvider.getString(SpinnerActions.UPDATE_EXERCISE.getStringId()))
        }
    }

    /** Update the selected spinner action with the provided value */
    fun updateSelectedSpinnerAction(actionText: String) {
        _selectedSpinnerAction.value = spinnerActions.first {
            resourceProvider.getString(it.getStringId()) == actionText
        }
    }

    /** Reset the data when the screen in which the view model is used is being destroyed */
    fun resetData() {
        selectedMuscleGroupId = 0
        updateMode(Mode.SELECT_MUSCLE_GROUP)
        _mGExercises.clear()
        _filteredmGExercises.value.clear()
    }

    /** Filter the muscle groups when the search value changes */
    fun updateSearch(value: String) {
        _search.value = value

        searchJob?.cancel()

        searchJob = viewModelScope.launch(Dispatchers.Default) {
            // Wait for the debounce time before filtering to avoid filtering on each letter
            delay(debounceTime)

            // Filter the muscle groups or the exercises
            if (mode.value == Mode.SELECT_MUSCLE_GROUP) {
                if (value.isEmpty()) {
                    _filteredMuscleGroups.value = _muscleGroups
                } else {
                    _filteredMuscleGroups.value = _muscleGroups.filter {
                        it.name.contains(value, ignoreCase = true)
                    } as MutableList<MuscleGroupModel>
                }
            } else {
                if (value.isEmpty()) {
                    _filteredmGExercises.value = _mGExercises
                } else {
                    _filteredmGExercises.value = _mGExercises.filter {
                        it.name.contains(value, ignoreCase = true)
                    } as MutableList<MGExerciseModel>
                }
            }
        }
    }

    /**
     * Select muscle group and show exercises for it
     * @param mgId the muscle group id to select
     */
    fun changeSelectedMuscleGroup(mgId: Long) {
        selectedMuscleGroupId = mgId

        if (selectedMuscleGroupId == 0L) {
            updateMode(Mode.SELECT_MUSCLE_GROUP)

        } else {
            updateMode(Mode.SELECT_EXERCISE)
        }
    }

    /**
     * Select the muscle group exercise and open the dialog to add this exercise
     * to the workout
     * @param mGExercise the muscle group exercise
     */
    fun selectMGExercise(mGExercise: MGExerciseModel) {
        viewModelScope.launch {
            val weightUnit =  userRepository.user.value!!.defaultValues.weightUnit.text

            DialogManager.showDialog(
                title = mGExercise.name,
                dialogName = "AddExerciseToWorkoutDialog",
                content = { AddExerciseToWorkoutDialog(
                    mGExercise = mGExercise,
                    weightUnit = weightUnit
                ) }
            )
        }
    }

    /** Populate the muscle groups */
    private fun populateMuscleGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            muscleGroupsRepository.getMuscleGroups(onSuccess = { muscleGroups ->
                _muscleGroups = muscleGroups
                _filteredMuscleGroups.value = _muscleGroups
            })
        }
    }

    /** Populate the muscle group exercises for the selected muscle group */
    private fun populateMGExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.getMuscleGroupExercises(
                muscleGroupId = selectedMuscleGroupId,
                onlyForUser = "N",
                onSuccess = { mGExercises ->
                    _mGExercises = mGExercises
                    _filteredmGExercises.value = _mGExercises
                }
            )
        }
    }
}