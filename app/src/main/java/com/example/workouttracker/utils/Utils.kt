package com.example.workouttracker.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Patterns
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.dialogs.AddEditWorkoutDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.utils.Constants.IMAGE_HEIGHT
import com.example.workouttracker.utils.Constants.IMAGE_WIDTH
import com.example.workouttracker.viewmodel.AddEditWorkoutModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Object with common methods */
object Utils {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

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

    /**
     * Return string in Base64 format from the provided bitmap
    * @param bitmap the bitmap
    */
    fun convertBitmapToString(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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
                content = { AddEditWorkoutDialog(workout = null, mode = AddEditWorkoutModel.ADD) }
            )
        }
    }

    /**
     * Scales a bitmap to fit within the specified width and height while maintaining aspect ratio.
     * Return the bitmap is success, null otherwise
     * @param bitmap the image bitmap
     */
    fun scaleBitmap(bitmap: Bitmap): Bitmap? {
        try {
            val width = bitmap.width
            val height = bitmap.height

            // Calculate the scaling factor while maintaining the aspect ratio
            val scaleFactor = minOf(width.toFloat() / width, height.toFloat() / height)

            return if (scaleFactor < 1) {
                val matrix = Matrix()
                matrix.postScale(scaleFactor, scaleFactor)

                // Create a new scaled bitmap
                Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            } else {
                // If the bitmap is already small, return it as is
                bitmap
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Create and scales a bitmap from the provided uri to fit within the specified width and height
     * while maintaining aspect ratio. Return the bitmap is success, null otherwise
     * @param uri the image uri
     */
    fun scaleBitmap(uri: Uri): Bitmap? {
        try {
            val contentResolver = appContext.contentResolver

            val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // For API 28+ (Pie and above), use ImageDecoder with scaling
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.setTargetSampleSize(4)
                }

            } else {
                // For older APIs, scale down using BitmapFactory
                val inputStream: InputStream? = contentResolver.openInputStream(uri)

                // Decode only the image dimensions first
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                // Calculate the appropriate sample size
                options.inSampleSize =
                    calculateInSampleSize(options.outWidth, options.outHeight)
                options.inJustDecodeBounds = false

                // Decode the scaled-down bitmap
                val scaledInputStream: InputStream? = contentResolver.openInputStream(uri)
                val scaledBitmap = BitmapFactory.decodeStream(scaledInputStream, null, options)
                scaledInputStream?.close()
                scaledBitmap!!
            }

            return bitmap

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /** Calculate a sample size to scale down the image.
     * @param rawWidth the actual image width
     * @param rawHeight the actual image height
     */
    private fun calculateInSampleSize(rawWidth: Int, rawHeight: Int): Int {
        var inSampleSize = 1

        if (rawHeight > IMAGE_HEIGHT || rawWidth > IMAGE_WIDTH) {
            val halfHeight = rawHeight / 2
            val halfWidth = rawWidth / 2

            while ((halfHeight / inSampleSize) >= IMAGE_HEIGHT && (halfWidth / inSampleSize) >= IMAGE_WIDTH) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}