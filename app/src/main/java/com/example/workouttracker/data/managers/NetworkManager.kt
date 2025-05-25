package com.example.workouttracker.data.managers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.workouttracker.R
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.data.network.CustomResponse
import com.example.workouttracker.ui.managers.LoadingManager
import com.example.workouttracker.ui.managers.SnackbarManager
import com.example.workouttracker.ui.managers.VibrationEvent
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.Constants
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/** Class to handle each request and execute the common errors logic when error occurs */
@Singleton
class NetworkManager @Inject constructor(
    private val context: Context,
    private val apiService: APIService
) {
    /** Use Factory pattern to create the call object. This is needed, because when
     * we need to refresh the token, the new token is returned as response from the server.
     * We must update it and execute the original request. We need a new instance of the
     * call object, when adding the token in AuthorizationInterceptor, the old token is used,
     * as OkHttpClient is immutable and does not take into account the change
     */
    private lateinit var requestFactory: () -> Call<CustomResponse>

    suspend fun sendRequest(request: () -> Call<CustomResponse>,
                            onSuccessCallback: (CustomResponse) -> Unit,
                            onErrorCallback: (CustomResponse) -> Unit = {}
    ) {
        if (!isNetworkAvailable()) {
            SnackbarManager.showSnackbar(R.string.error_msg_no_internet)
            VibrationManager.makeVibration()
            onErrorCallback(getEmptyResponse())
            return
        }

        // Update the request factory property
        requestFactory = request

        // Execute the call, passing new Call<CustomResponse> object
        execute(requestFactory(), onSuccessCallback, onErrorCallback)
    }

    /** Execute to request
     * @param request the request to send
     * @param onSuccessCallback the callback to execute on success
     * @param onErrorCallback the callback to execute if the response code is not success
     */
    private suspend fun execute(request: Call<CustomResponse>,
                                onSuccessCallback: (CustomResponse) -> Unit,
                                onErrorCallback: (CustomResponse) -> Unit = {}
    ) {
        LoadingManager.showLoading()

        val response: Response<CustomResponse>
        val responseContent: CustomResponse?

        try {
            response = request.awaitResponse()

            if (response.isSuccessful && response.body() != null) {
                // Request is successful and body is not empty,
                // execute the success callback
                onSuccessCallback(response.body()!!)

            } else if (response.errorBody() != null) {
                // Extract the error body which must contain CustomResponse and set the
                // responseBody which will be processed in onError(onErrorCallback)
                val errorBody = JSONObject(response.errorBody()!!.string())

                responseContent = if (errorBody.optJSONObject("value") != null) {
                    Gson().fromJson(errorBody.getJSONObject("value").toString(), CustomResponse::class.java)
                } else {
                    Gson().fromJson(errorBody.toString(), CustomResponse::class.java)
                }

                if (responseContent == null || responseContent.code == 0) {
                    // Something went wrong, show unexpected error and try to execute
                    // on error callback
                    onError(getEmptyResponse(), onErrorCallback)
                    return
                }

                // Execute on error callback
                if (responseContent.code == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    if (responseContent.data.size == 1) {
                        // Update the token using the returned token
                        apiService.updateToken(responseContent.data[0])

                        // Remove the progress dialog from the "first" request before resending it
                        LoadingManager.hideLoading()

                        // Resend the original request
                        sendRequest(requestFactory, onSuccessCallback, onErrorCallback)

                    } else {
                         onError(responseContent, onErrorCallback)
                    }
                } else {
                    // Execute the on error callback if the request failed for some reason
                    // and show the error message
                    onError(responseContent, onErrorCallback)
                }

            } else {
                onError(getEmptyResponse(), onErrorCallback)
                return
            }

        } catch (e: Exception) {
            val errorResponse = if (e is SocketTimeoutException) {
                getTimeoutResponse()
            } else {
                 getEmptyResponse()
            }

            onError(errorResponse, onErrorCallback)
            e.printStackTrace()

        } finally {
            // Try to update the notification indication
//                if (responseContent != null && user.value != null &&
//                    responseContent.notification != notification.value) {
//                    updateNotification(responseContent.notification)
//                }

            // Remove the progress dialog
            LoadingManager.hideLoading()
        }
    }

    /** Executes the logic when request error occurs
     * @param responseContent the response content
     * @param onErrorCallback the callback to execute
     */
    private suspend fun onError(responseContent: CustomResponse?, onErrorCallback: (CustomResponse) -> Unit) {
        var message = ""

        if (responseContent != null) {
            // Execute the callback
            onErrorCallback(responseContent)

            if (responseContent.message.isNotEmpty()) {
                message = responseContent.message
            }
        } else {
            onErrorCallback(getEmptyResponse())
        }

        if (message.isEmpty()) {
            SnackbarManager.showSnackbar(R.string.error_msg_unexpected)
        } else {
            SnackbarManager.showSnackbar(message)
        }

        VibrationManager.makeVibration(VibrationEvent(pattern = Constants.REQUEST_ERROR_VIBRATION))
    }

    /** Create CustomResponse object with fail code when CustomResponse is not available */
    private fun getEmptyResponse(): CustomResponse {
        return CustomResponse(HttpURLConnection.HTTP_BAD_REQUEST, "", listOf(), false)
    }

    /** Create CustomResponse object with fail code when response is request timeout */
    private fun getTimeoutResponse(): CustomResponse {
         return CustomResponse(
                    HttpURLConnection.HTTP_INTERNAL_ERROR,
                    context.getString(R.string.error_msg_unexpected_network_problem),
                    listOf(), false
         )
    }

    /**
     * Utility function to check if the wifi / cellular data is enabled
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        val wifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

        if (wifi) {
            return true
        }

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}