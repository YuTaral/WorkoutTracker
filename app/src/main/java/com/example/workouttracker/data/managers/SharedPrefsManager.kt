package com.example.workouttracker.data.managers

import com.example.workouttracker.utils.Constants.AUTH_TOKEN_KEY
import com.example.workouttracker.utils.Constants.FIRST_START_KEY
import com.example.workouttracker.utils.Constants.SECURE_PREFS_FILE_NAME
import com.example.workouttracker.utils.Constants.SERIALIZED_USER_KEY
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.workouttracker.data.models.UserModel
import com.example.workouttracker.utils.Utils
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SharedPrefsManager class that handles reading and writing data to encrypted shared preferences.
 * This class manages storing and retrieving sensitive information such as authorization tokens,
 * user data, and app initialization state securely.
 */
@Singleton
class SharedPrefsManager @Inject constructor(
    private val context: Context
) {

    /**
     * Retrieves the stored authorization token from shared preferences.
     * Returns an empty string if no token is found.
     *
     * @return the stored authorization token or an empty string if not found
     */
    fun getStoredToken(): String {
        return getSharedPref().getString(AUTH_TOKEN_KEY, null) ?: ""
    }

    /**
     * Retrieves the stored user model from shared preferences.
     * Returns null if no user data is found.
     * @return the stored UserModel or null if not found
     */
    fun getStoredUser(): UserModel? {
        val serializedUser = getSharedPref().getString(SERIALIZED_USER_KEY, "") ?: ""

        return if (serializedUser.isEmpty()) {
            null
        } else {
            UserModel(serializedUser)
        }
    }

    /**
     * Updates the authorization token in shared preferences.
     * If the token is an empty string, the token is removed.
     * @param token the token to save, or an empty string to remove the token
     */
    fun updateTokenInPrefs(token: String) {
        val sharedPref = getSharedPref()

        if (token.isEmpty()) {
            sharedPref.edit { remove(AUTH_TOKEN_KEY) }
        } else {
            sharedPref.edit { putString(AUTH_TOKEN_KEY, token) }
        }
    }

    /**
     * Updates the serialized user model in shared preferences.
     * If the user model is null, it removes the user data from preferences.
     * @param model the user model to save, or null to remove the user data
     */
    fun updateUserInPrefs(model: UserModel?) {
        val sharedPref = getSharedPref()

        if (model == null) {
            sharedPref.edit { remove(SERIALIZED_USER_KEY) }
        } else {
            sharedPref.edit { putString(SERIALIZED_USER_KEY, Utils.serializeObject(model)) }
        }
    }

    /**
     * Checks if this is the first time the app is started on the device.
     * @return true if it's the first time the app is started, false otherwise
     */
    fun isFirstAppStart(): Boolean {
        val firstTime = getSharedPref().getString(FIRST_START_KEY, null) ?: ""
        return firstTime.isEmpty()
    }

    /**
     * Sets the flag to indicate that the app has been started at least once.
     */
    fun setFirstStartApp() {
        getSharedPref().edit { putString(FIRST_START_KEY, "N") }
    }

    /**
     * Creates and returns an EncryptedSharedPreferences instance.
     * It retries if there is an error during the creation, such as when the MasterKey
     * or EncryptedSharedPreferences is not working correctly.
     */
    private fun getSharedPref(): SharedPreferences {
        return try {
            createSharedPrefs()
        } catch (e: Exception) {
            // If an error occurs, retry by deleting the corrupted file and creating new shared preferences
            context.deleteSharedPreferences(SECURE_PREFS_FILE_NAME)
            createSharedPrefs()
        }
    }

    /**
     * Creates and returns an instance of EncryptedSharedPreferences.
     * @return an EncryptedSharedPreferences instance
     */
    private fun createSharedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            SECURE_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
