package com.example.workouttracker.ui.managers

import com.example.workouttracker.utils.interfaces.IImagePicker
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Singleton class to expose flow to allow emitting events to pick image */
@Singleton
class ImagePickerEventBus @Inject constructor() {

    /** Flow to emit events to show the image picker */
    private val _imagePickerRequests = MutableSharedFlow<IImagePicker>()
    val imagePickerRequests = _imagePickerRequests.asSharedFlow()

    /** Emit event to show the image picker */
    suspend fun requestImagePicker(picker: IImagePicker) {
        _imagePickerRequests.emit(picker)
    }
}