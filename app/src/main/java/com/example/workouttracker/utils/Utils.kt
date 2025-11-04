package com.example.workouttracker.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.util.Patterns
import com.example.workouttracker.utils.Constants.IMAGE_HEIGHT
import com.example.workouttracker.utils.Constants.IMAGE_WIDTH
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
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

    /**
     * Scales a bitmap to fit within the specified width and height while maintaining aspect ratio.
     * Return the bitmap is success, null otherwise
     * @param bitmap the image bitmap
     * @param onException callback to execute in case of exception
     */
    fun scaleBitmap(bitmap: Bitmap, onException: (Exception) -> Unit): Bitmap? {
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
            Log.e("ScaleBitmap", "Scale Bitmap failed", e)
            onException(e)
            return null
        }
    }

    /**
     * Create and scales a bitmap from the provided uri to fit within the specified width and height
     * while maintaining aspect ratio. Return the bitmap is success, null otherwise
     * @param uri the image uri
     * @param contentResolver ContentResolver instance, provided by the activity
     * @param onException callback to execute in case of exception
     */
    fun scaleBitmap(uri: Uri, contentResolver: ContentResolver, onException: (Exception) -> Unit): Bitmap? {
        try {
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
            Log.e("ScaleBitmap", "Scale Bitmap failed", e)
            onException(e)
            return null
        }
    }

    /**
     * Return the days difference between the two dates
     * @param date1 the first date
     * @param date2 the second date
     */
    fun getDateDifferenceInDays(date1: Date, date2: Date): Long {
        val cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal1.time = date1
        cal1.set(Calendar.HOUR_OF_DAY, 0)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        cal1.set(Calendar.MILLISECOND, 0)

        val cal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal2.time = date2
        cal2.set(Calendar.HOUR_OF_DAY, 0)
        cal2.set(Calendar.MINUTE, 0)
        cal2.set(Calendar.SECOND, 0)
        cal2.set(Calendar.MILLISECOND, 0)

        val diffMillis = cal1.timeInMillis - cal2.timeInMillis
        return diffMillis / (24 * 60 * 60 * 1000)
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