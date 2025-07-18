package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** WorkoutTemplateRepository class, used to execute all requests related to workout templates */
class WorkoutTemplatesRepository @Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {

    /** The user templates */
    private var _templates = MutableStateFlow<MutableList<WorkoutModel>>(mutableListOf())
    val templates = _templates.asStateFlow()

    /** Add new workout template
     * @param template the workout template data
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun addWorkoutTemplate(template: WorkoutModel, onSuccess: () -> Unit) {
        val params = mapOf("workout" to Utils.serializeObject(template))

        networkManager.sendRequest(
            request = { apiService.getInstance().addWorkoutTemplate(params) },
            onSuccessCallback = { response -> onSuccess() }
        )
    }

    /** Delete the workout template
     * @param id the template id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun deleteWorkoutTemplate(id: Long) {
        networkManager.sendRequest(
            request = { apiService.getInstance().deleteWorkoutTemplate(id) },
            onSuccessCallback = { response ->
                refreshTemplates(response.data.map { WorkoutModel(it) }.toMutableList())
            }
        )
    }

    /** Update the workout template
     * @param template the workout template data
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun updateWorkoutTemplate(template: WorkoutModel, onSuccess: () -> Unit) {
        val params = mapOf("workout" to Utils.serializeObject(template))

        networkManager.sendRequest(
            request = { apiService.getInstance().updateWorkoutTemplate(params) },
            onSuccessCallback = { response ->
                refreshTemplates(response.data.map { WorkoutModel(it) }.toMutableList())
                onSuccess()
            }
        )
    }

    /** Fetch workout templates which has been added by the user */
    suspend fun refreshTemplates() {
        networkManager.sendRequest(
            request = { apiService.getInstance().getWorkoutTemplates() },
            onSuccessCallback = { response ->
                _templates.value = (response.data.map { WorkoutModel(it) } as MutableList<WorkoutModel>)
            }
        )
    }

    /** Update the templates from the provided value */
    fun refreshTemplates(newTemplates: MutableList<WorkoutModel>) {
        _templates.value = newTemplates
    }

    /**
     * Get the template data
     * @param assignedWorkoutId the assigned workout id
     */
    suspend fun getTemplate(assignedWorkoutId: Long, onSuccess: (WorkoutModel) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getWorkoutTemplate(assignedWorkoutId = assignedWorkoutId) },
            onSuccessCallback = { onSuccess(WorkoutModel(it.data[0]))}
        )
    }
}