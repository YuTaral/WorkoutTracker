package com.example.workouttracker.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Patterns
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.dialogs.AddEditWorkoutDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Object with common methods */
object Utils {

    /** Email validation
     * @param target the email to check
     */
    fun isValidEmail(target: CharSequence?): Boolean {
        return !target.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    /** JSON serializes an object using Gson and returns it
     * @param obj the object to serialize
     */
    fun serializeObject(obj: Any): String {
        val gson = Gson()
        return gson.toJson(obj)
    }

    /** Return bitmap from the provided string
     * @param image the image sa Base64 string
     */
    fun convertStringToBitmap(image: String): Bitmap {
        val imageBytes = Base64.decode(image, Base64.DEFAULT)

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    /** Convert the date to default app format date and time - dd/MMM/yyyy
     * @param date the date to format
     */
    fun defaultFormatDateTime(date: Date): String {
        return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(date)
    }

    /** Format a double value depending on the value after the decimal point.
     * @param value the value
     */
    @SuppressLint("DefaultLocale")
    fun formatDouble(value: Double): String {
        return if (value % 1 == 0.0) {
            String.format("%.0f", value)
        } else {
            String.format("%.3f", value)
        }
    }

    /** Return ISO08601 formatted date from the Date object
     * @param date the date
     */
    fun formatDateToISO8601(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    /**
     * Convert the date to default app format date - dd/MMM/yyyy
     * @param date the date to format
     */
    fun defaultFormatDate(date: Date): String {
        val localFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        localFormatter.timeZone = TimeZone.getDefault()

        return localFormatter.format(date)
    }

    /** Display the add workout dialog */
    fun showAddWorkoutDialog(viewModelScope: CoroutineScope, resourceProvider: ResourceProvider) {
        viewModelScope.launch {
            DialogManager.showDialog(
                title = resourceProvider.getString(R.string.add_workout_title),
                dialogName = "AddEditWorkoutDialog",
                content = { AddEditWorkoutDialog(null) }
            )
        }
    }
}