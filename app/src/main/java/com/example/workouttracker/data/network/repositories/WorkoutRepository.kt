package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

/** WorkoutRepository class, used to execute all requests related to workouts */
class WorkoutRepository @Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {
    /** The latest user's workouts */
    private var _workouts = MutableStateFlow<MutableList<WorkoutModel>>(mutableListOf())
    var workouts = _workouts.asStateFlow()

    /** The currently selected workout */
    private var _selectedWorkout = MutableStateFlow<WorkoutModel?>(null)
    var selectedWorkout = _selectedWorkout.asStateFlow()

    /** The workout start date. Optionally updated when update workouts is called */
    private lateinit var startDate: Date

    /** The workout show scheduled flag. Optionally updated when update workouts is called */
    private var showScheduled: Boolean = false

    /** Add new workout
     * @param workout the workout data
     * @param assignedWorkoutId larger than 0 if the workout is not started from assignment
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun addWorkout(workout: WorkoutModel, assignedWorkoutId: Long, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("workout" to Utils.serializeObject(workout), "assignedWorkoutId" to assignedWorkoutId.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().addWorkout(params) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Edit the workout
     * @param workout the workout data
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun updateWorkout(workout: WorkoutModel, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("workout" to Utils.serializeObject(workout))

        networkManager.sendRequest(
            request = { apiService.getInstance().updateWorkout(params) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Finish the workout
     * @param workoutId the workout id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun finishWorkout(workoutId: Long, onSuccess: (WorkoutModel) -> Unit) {
        networkManager.sendRequest(
            request = {
                apiService.getInstance().finishWorkout(workoutId)
          },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Delete the workout
     * @param workoutId the workout id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun deleteWorkout(workoutId: Long, onSuccess: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().deleteWorkout(workoutId) },
            onSuccessCallback = { onSuccess() })
    }

    /** Update the latest the workouts
     * @param newStartDate the start date
     * @param newShowScheduled true to show scheduled, false otherwise (may be null - no change is needed)
     */
    suspend fun updateWorkouts(newStartDate: Date?, newShowScheduled: Boolean?) {
        if (newStartDate != null) {
            startDate = newStartDate
        }

        if (newShowScheduled != null) {
            showScheduled = newShowScheduled
        }

        networkManager.sendRequest(
            request = { apiService.getInstance().getWorkouts(Utils.formatDateToISO8601(startDate), showScheduled) },
            onSuccessCallback = { response ->
                _workouts.value = response.data.map { WorkoutModel(it) }.toMutableList()
            }
        )
    }

    /**
     * Mark the workout as selected
     * @param workout selected workout, may be null (when deleted)
     */
    fun updateSelectedWorkout(workout: WorkoutModel?) {
        _selectedWorkout.value = workout
    }
}