package com.example.workouttracker.utils.interfaces

import android.graphics.Bitmap

/**
 * IImagePicker interface to define the callbacks to execute when image upload is successful / failed */
interface IImagePicker {
    fun onImageUploadSuccess(bitmap: Bitmap)
    fun onImageUploadFail()
}