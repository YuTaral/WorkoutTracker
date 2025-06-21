package com.example.workouttracker.ui

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.lifecycle.LifecycleCoroutineScope

/**
 * Interface to be implement by the Main Activity and provide only the necessary
 * methods to the PermissionResultHandler
 */
interface PermissionHost {
    /**
     * Returns the lifecycle-aware coroutine scope tied to the activity.
     * @return The LifecycleCoroutineScope associated with the activity.
     */
    fun getLifecycleScope(): LifecycleCoroutineScope

    /**
     * Registers a permission request launcher and returns the corresponding ActivityResultLauncher.
     * @param callback The callback to receive the result of the permission request (true if granted).
     * @return An ActivityResultLauncher for requesting permissions.
     */
    fun registerPermissionLauncher(callback: (Boolean) -> Unit): ActivityResultLauncher<String>

    /**
     * Registers an activity result launcher for starting intents and returns the launcher.
     * @param callback The callback to receive the activity result.
     * @return An ActivityResultLauncher for launching intents.
     */
    fun registerActivityResultLauncher(callback: (ActivityResult) -> Unit): ActivityResultLauncher<Intent>

    /**
     * Registers a photo picker launcher and returns the corresponding ActivityResultLauncher.
     * @param callback The callback to receive the selected image URI or null if none selected.
     * @return An ActivityResultLauncher for picking visual media.
     */
    fun registerPhotoPickerLauncher(callback: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest>

    /**
     * Returns the package name of the app for use in intent URIs.
     * @return The package name as a String.
     */
    fun getPackageName(): String

    /**
     * Returns true if a rationale for the specified permission should be shown to the user.
     * @param permission The permission being requested.
     * @return True if a rationale should be shown, false otherwise.
     */
    fun shouldShowRequestPermissionRationale(permission: String): Boolean

    /**
     * Starts the given intent from the activity.
     * @param intent The intent to start.
     */
    fun startActivity(intent: Intent)

    /**
     * Returns true if the specified permission is currently granted.
     * @param permission The permission to check.
     * @return True if the permission is granted, false otherwise.
     */
    fun checkPermissionGranted(permission: String): Boolean

    /** Return content resolver */
    fun getContentResolver(): ContentResolver
}