package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.models.ExerciseModel
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.data.network.CustomResponse
import com.example.workouttracker.utils.Utils
import javax.inject.Inject

/** ExerciseRepository class, used to execute all requests related to exercise */
class ExerciseRepository @Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {

    /** Add new exercise to the given workout
     * @param exercise the exercise data
     * @param workoutId the workout id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun addExerciseToWorkout(exercise: ExerciseModel, workoutId: Long, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to workoutId.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().addExerciseToWorkout(params) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Update the exercise from the given workout
     * @param exercise the exercise data
     * @param workoutId the workout id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun updateExerciseFromWorkout(exercise: ExerciseModel, workoutId: Long, onSuccess: (WorkoutModel) -> Unit) {
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to workoutId.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().updateExerciseFromWorkout(params) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Delete the exercise from the workout
     * @param exerciseId the exercise id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun deleteExerciseFromWorkout(exerciseId: Long, onSuccess: (WorkoutModel) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().deleteExerciseFromWorkout(exerciseId) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Fetch all exercises for this muscle group
     * @param muscleGroupId the muscle group id
     * @param onlyForUser "Y" if we need only user defined muscle group exercises, "N" if we need
     * user defined and the default ones
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun getMuscleGroupExercises(muscleGroupId: Long, onlyForUser: String, onSuccess:(MutableList<MGExerciseModel>) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getExerciseByMGId(muscleGroupId, onlyForUser) },
            onSuccessCallback = { response -> onSuccess(response.data.map { MGExerciseModel(it) }.toMutableList()) }
        )
    }

    /** Add new exercise
     * @param exercise the exercise data
     * @param workoutId greater than 0 to add the newly created exercise to the current workout, 0 otherwise
     * @param onlyForUser used when workoutId is 0. "Y" if we need only user defined muscle group exercises
     * to be returned to the client, "N" if we need user defined and the default ones
     * @param checkExistingEx "Y" when upon exercise creation check whether exercise with the same name
     * already exists must be executed, "N" otherwise
     * @param onSuccess callback to execute if request is successful
     * @param onFailure callback to execute if request failed
     */
    suspend fun addExercise(exercise: MGExerciseModel, workoutId: String, onlyForUser: String, checkExistingEx: String,
                    onSuccess: (List<String>) -> Unit, onFailure: (CustomResponse) -> Unit) {

        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to workoutId,
                            "onlyForUser" to onlyForUser, "checkExistingEx" to checkExistingEx)

        networkManager.sendRequest(
            request = { apiService.getInstance().addExercise(params) },
            onSuccessCallback = { response -> onSuccess(response.data) },
            onErrorCallback = { response -> onFailure(response) }
        )
    }

    /** Update the exercise
     * @param exercise the exercise data
     * @param onlyForUser "Y" if the returned exercises should be only user defined, "N" if all
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun updateExercise(exercise: MGExerciseModel, onlyForUser: String, onSuccess: (List<String>) -> Unit) {
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "onlyForUser" to onlyForUser)

        networkManager.sendRequest(
            request = { apiService.getInstance().updateExercise(params) },
            onSuccessCallback = { response ->
                onSuccess(response.data)
            }
        )
    }

    /** Delete the exercise
     * @param mGExerciseId the muscle group exercise id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun deleteExercise(mGExerciseId: Long, onSuccess: (List<MGExerciseModel>) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().deleteExercise(mGExerciseId) },
            onSuccessCallback = { response ->
                onSuccess(response.data.map { MGExerciseModel(it) })
            }
        )
    }

    /** Mark the set as completed
     * @param id the set id to complete
     * @param workoutId the selected workout id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun completeSet(id: Long, workoutId: Long,onSuccess: (WorkoutModel) -> Unit) {
        val params = mapOf("id" to id.toString(), "workoutId" to workoutId.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().completeSet(params) },
            onSuccessCallback = { response ->
                onSuccess(WorkoutModel(response.data[0]))
            }
        )
    }

    /** Fetch specific muscle group exercise
     * @param mGExerciseId the muscle group exercise id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun getMGExercise(mGExerciseId: Long, onSuccess:(MGExerciseModel) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getMGExercise(mGExerciseId) },
            onSuccessCallback = { response -> onSuccess(MGExerciseModel(response.data[0])) }
        )
    }
}