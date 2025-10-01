package com.example.workouttracker.data.network

import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * APIService is a singleton class responsible for managing network requests through Retrofit.
 * It handles the creation of the Retrofit client and the injection of the authorization token
 * into the request headers. It also provides a method to update the token if needed.
 * This class is injected by Hilt to ensure proper lifecycle management and dependency injection.
 * @param sharedPrefsManager A shared preference manager that stores and retrieves the JWT token.
 */
@Singleton
class APIService @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager
) {
    private var retrofit: Retrofit = createRetrofitClient(sharedPrefsManager.getStoredToken())

    /**
     * Creates a Retrofit client with a custom OkHttpClient. The client includes an interceptor
     * to add the authorization token to the headers if the token is not empty.
     * @param token The JWT token to be included in the Authorization header.
     * @return A Retrofit instance configured with the appropriate client and base URL.
     */
    private fun createRetrofitClient(token: String): Retrofit {
        val client = OkHttpClient.Builder()
            .apply {
                if (token.isNotEmpty()) {
                    addInterceptor(AuthorizationInterceptor(token))
                }
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Updates the current authorization token used in the API service. This will reinitialize
     * the Retrofit client with a new OkHttpClient that includes the new token.
     * @param token The new JWT token to be included in the Authorization header.
     */
    fun updateToken(token: String) {
        retrofit = createRetrofitClient(token)
    }

    /**
     * Returns the current instance of the IAPIService, which is a Retrofit interface
     * that makes the network requests. This method ensures that a valid instance is provided
     * by initializing Retrofit with the stored token if needed.
     * @return The instance of IAPIService used to make network requests.
     */
    fun getInstance(): IAPIService {
        return retrofit.create(IAPIService::class.java)
    }
}
