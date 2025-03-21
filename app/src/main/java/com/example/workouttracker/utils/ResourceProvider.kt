package com.example.workouttracker.utils

import android.content.Context
import android.support.annotation.StringRes
import jakarta.inject.Inject

/** Injectable Resource provider class to allow retrieving string from resources in view models */
class ResourceProvider @Inject constructor(
    private val context: Context
) {
    fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }
}