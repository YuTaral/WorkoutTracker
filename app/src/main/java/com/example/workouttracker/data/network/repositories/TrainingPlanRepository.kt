package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.models.TrainingDayModel
import com.example.workouttracker.data.models.TrainingPlanModel
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** TrainingPlanRepository class, used to execute all requests related to programs */
class TrainingPlanRepository@Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {
    /** The user's training programs */
    private var _trainingPlans = MutableStateFlow<MutableList<TrainingPlanModel>>(mutableListOf())
    var trainingPlans = _trainingPlans.asStateFlow()

    /** The selected training program if any */
    private var _selectedTrainingPlan = MutableStateFlow<TrainingPlanModel?>(null)
    var selectedTrainingPlan = _selectedTrainingPlan.asStateFlow()

    /** Refresh the data for training programs */
    suspend fun refreshTrainingPlans() {
        networkManager.sendRequest(
            request = { apiService.getInstance().refreshTrainingProgram() },
            onSuccessCallback = { response ->
                _trainingPlans.value = response.data.map { TrainingPlanModel(it) }.toMutableList()
            }
        )
    }

    /**
     * Add the new training program
     * @param trainingProgram the training program to add
     * @param onSuccess callback to execute on success
     */
    suspend fun addTrainingPlan(trainingProgram: TrainingPlanModel, onSuccess: (TrainingPlanModel) -> Unit) {
        val params = mapOf("trainingProgram" to Utils.serializeObject(trainingProgram))

        return networkManager.sendRequest(
            request = { apiService.getInstance().addTrainingProgram(params) },
            onSuccessCallback = { onSuccess(TrainingPlanModel(it.data[0])) }
        )
    }

    /**
     * Update the training program
     * @param trainingProgram the training program to update
     * @param onSuccess callback to execute on success
     */
    suspend fun updateTrainingPlan(trainingProgram: TrainingPlanModel, onSuccess: (TrainingPlanModel) -> Unit) {
        val params = mapOf("trainingProgram" to Utils.serializeObject(trainingProgram))

        return networkManager.sendRequest(
            request = { apiService.getInstance().updateTrainingProgram(params) },
            onSuccessCallback = { onSuccess(TrainingPlanModel(it.data[0])) }
        )
    }

    /**
     * Delete the training program
     * @param trainingProgramId the training program id to delete
     * @param onSuccess callback to execute on success
     */
    suspend fun deleteTrainingPlan(trainingProgramId: Long, onSuccess: () -> Unit) {
        return networkManager.sendRequest(
            request = { apiService.getInstance().deleteTrainingProgram(trainingProgramId) },
            onSuccessCallback = { onSuccess() }
        )
    }

    /**
     * Add the training day to the program
     * @param trainingDay the training day to add
     * @param onSuccess callback to execute on success
     */
    suspend fun addTrainingDayToPlan(trainingDay: TrainingDayModel, onSuccess: (TrainingPlanModel) -> Unit) {
        val params = mapOf("trainingDay" to Utils.serializeObject(trainingDay))

        return networkManager.sendRequest(
            request = { apiService.getInstance().addTrainingDayToProgram(params) },
            onSuccessCallback = { onSuccess(TrainingPlanModel(it.data[0])) }
        )
    }

    /**
     * Update the training day
     * @param trainingDay the training day to update
     * @param onSuccess callback to execute on success
     */
    suspend fun updateTrainingDayToPlan(trainingDay: TrainingDayModel, onSuccess: (TrainingPlanModel) -> Unit) {
        val params = mapOf("trainingDay" to Utils.serializeObject(trainingDay))

        return networkManager.sendRequest(
            request = { apiService.getInstance().updateTrainingDayToProgram(params) },
            onSuccessCallback = { onSuccess(TrainingPlanModel(it.data[0])) }
        )
    }

    /**
     * Delete the training day
     * @param trainingDayId the training day to delete
     * @param onSuccess callback to execute on success
     */
    suspend fun deleteTrainingDay(trainingDayId: Long, onSuccess: (TrainingPlanModel) -> Unit) {
        return networkManager.sendRequest(
            request = { apiService.getInstance().deleteTrainingDay(trainingDayId) },
            onSuccessCallback = { onSuccess(TrainingPlanModel(it.data[0])) }
        )
    }

    /**
     * Assign the training plan to the members
     * @param trainingPlanId the training id
     * @param memberIds the member ids
     * @param onSuccess callback to execute on success
     */
    suspend fun assignTrainingPlan(trainingPlanId: Long, memberIds: List<Long>, onSuccess: () -> Unit) {
        val params = mapOf("trainingPlanId" to trainingPlanId.toString(), "memberIds" to memberIds.toString())

        networkManager.sendRequest(
            request = { apiService.getInstance().assignTrainingPlan(params) },
            onSuccessCallback = { onSuccess() }
        )
    }

    /**
     * Update selected training program
     * @param trainingProgram the training program to update
     */
    fun updateSelectedTrainingPlan(trainingProgram: TrainingPlanModel?) {
        _selectedTrainingPlan.value = trainingProgram
    }
}