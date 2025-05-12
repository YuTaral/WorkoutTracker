package com.example.workouttracker.data.network.repositories

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.models.UserDefaultValuesModel
import com.example.workouttracker.data.models.UserModel
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.utils.Utils
import javax.inject.Inject

/** UserProfileRepository class, used to execute all requests related to User Profile */
class UserProfileRepository @Inject constructor(
    private val apiService: APIService,
    private val networkManager: NetworkManager
) {

    /** Change user default exercise values
     * @param values the data
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun updateUserDefaultValues(values: UserDefaultValuesModel, onSuccess: (UserDefaultValuesModel) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().updateUserDefaultValues(mapOf("values" to Utils.serializeObject(values))) },
            onSuccessCallback = { response -> onSuccess(UserDefaultValuesModel(response.data[0])) }
        )
    }

    /** Send a request to fetch the default values for the exercise. If there are no default values
     * for the specific exercise, the request return the user default values.
     * @param mgExerciseId the muscle group exercise id
     * @param onSuccess callback to execute if request is successful
     */
    suspend fun getUserDefaultValues(mgExerciseId: Long, onSuccess: (UserDefaultValuesModel) -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().getUserDefaultValues(mgExerciseId) },
            onSuccessCallback = { response -> onSuccess(UserDefaultValuesModel(response.data[0]))}
        )
    }

    /** Update the user profile
     * @param user the user
     * @param onSuccess callback to execute if request is successful
     * @param onFailure callback to execute if request failed
     */
    suspend fun updateUserProfile(user: UserModel, onSuccess: (UserModel) -> Unit, onFailure: () -> Unit) {
        networkManager.sendRequest(
            request = { apiService.getInstance().updateUserProfile(mapOf("user" to Utils.serializeObject(user))) },
            onSuccessCallback = { response -> onSuccess(UserModel(response.data[0]))},
            onErrorCallback = { onFailure() }
        )
    }
}