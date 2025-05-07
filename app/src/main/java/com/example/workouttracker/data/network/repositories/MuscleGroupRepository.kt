package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.data.network.APIService
import javax.inject.Inject


/** MuscleGroupRepository class, used to execute all requests related to muscle groups */
class MuscleGroupRepository @Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {

    /**
     * Fetch muscle groups
     * @param onSuccess callback to execute on success
     */
    suspend fun getMuscleGroups(onSuccess: (MutableList<MuscleGroupModel>) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getMuscleGroups() },
            onSuccessCallback = { response ->
                onSuccess(response.data.map { MuscleGroupModel(it) }.toMutableList())
            }
        )
    }

}