package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.models.TrainingDayModel
import com.example.workouttracker.data.models.TrainingProgramModel
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** TrainingProgramRepository class, used to execute all requests related to programs */
class TrainingProgramRepository@Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {
    /** The user's training programs */
    private var _trainingPrograms = MutableStateFlow<MutableList<TrainingProgramModel>>(mutableListOf())
    var trainingPrograms = _trainingPrograms.asStateFlow()

    /** The selected training program if any */
    private var _selectedTrainingProgram = MutableStateFlow<TrainingProgramModel?>(null)
    var selectedTrainingProgram = _selectedTrainingProgram.asStateFlow()

    /** Refresh the data for training programs */
    suspend fun refreshTrainingPrograms() {
        networkManager.sendRequest(
            request = { apiService.getInstance().refreshTrainingProgram() },
            onSuccessCallback = { response ->
                _trainingPrograms.value = response.data.map { TrainingProgramModel(it) }.toMutableList()
            }
        )
    }

    /**
     * Add the new training program
     * @param trainingProgram the training program to add
     * @param onSuccess callback to execute on success
     */
    suspend fun addTrainingProgram(trainingProgram: TrainingProgramModel, onSuccess: (TrainingProgramModel) -> Unit) {
        val params = mapOf("trainingProgram" to Utils.serializeObject(trainingProgram))

        return networkManager.sendRequest(
            request = { apiService.getInstance().addTrainingProgram(params) },
            onSuccessCallback = { onSuccess(TrainingProgramModel(it.data[0])) }
        )
    }

    /**
     * Update the training program
     * @param trainingProgram the training program to update
     * @param onSuccess callback to execute on success
     */
    suspend fun updateTrainingProgram(trainingProgram: TrainingProgramModel, onSuccess: (TrainingProgramModel) -> Unit) {
        val params = mapOf("trainingProgram" to Utils.serializeObject(trainingProgram))

        return networkManager.sendRequest(
            request = { apiService.getInstance().updateTrainingProgram(params) },
            onSuccessCallback = { onSuccess(TrainingProgramModel(it.data[0])) }
        )
    }

    /**
     * Delete the training program
     * @param trainingProgramId the training program id to delete
     * @param onSuccess callback to execute on success
     */
    suspend fun deleteTrainingProgram(trainingProgramId: Long, onSuccess: () -> Unit) {
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
    suspend fun addTrainingDayToProgram(trainingDay: TrainingDayModel, onSuccess: (TrainingProgramModel) -> Unit) {
        val params = mapOf("trainingDay" to Utils.serializeObject(trainingDay))

        return networkManager.sendRequest(
            request = { apiService.getInstance().addTrainingDayToProgram(params) },
            onSuccessCallback = { onSuccess(TrainingProgramModel(it.data[0])) }
        )
    }

    /**
     * Update the training day
     * @param trainingDay the training day to update
     * @param onSuccess callback to execute on success
     */
    suspend fun updateTrainingDayToProgram(trainingDay: TrainingDayModel, onSuccess: (TrainingProgramModel) -> Unit) {
        val params = mapOf("trainingDay" to Utils.serializeObject(trainingDay))

        return networkManager.sendRequest(
            request = { apiService.getInstance().updateTrainingDayToProgram(params) },
            onSuccessCallback = { onSuccess(TrainingProgramModel(it.data[0])) }
        )
    }

    /**
     * Delete the training day
     * @param trainingDayId the training day to delete
     * @param onSuccess callback to execute on success
     */
    suspend fun deleteTrainingDay(trainingDayId: Long, onSuccess: (TrainingProgramModel) -> Unit) {
        return networkManager.sendRequest(
            request = { apiService.getInstance().deleteTrainingDay(trainingDayId) },
            onSuccessCallback = { onSuccess(TrainingProgramModel(it.data[0])) }
        )
    }

    /**
     * Update selected training program
     * @param trainingProgram the training program to update
     */
    fun updateSelectedTrainingProgram(trainingProgram: TrainingProgramModel?) {
        _selectedTrainingProgram.value = trainingProgram
    }
}