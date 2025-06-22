package com.example.workouttracker.ui.managers

import com.example.workouttracker.utils.Constants.VALIDATION_FAILED_VIBRATION
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Vibration event class */
data class VibrationEvent(
    val pattern: LongArray,
    val repeat: Int = -1
)

/** Class to handle vibrations logic to warn the user when needed */
@Singleton
class VibrationManager @Inject constructor() {

    /** Flow to emit events to make vibration */
    private val _events = MutableSharedFlow<VibrationEvent>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /** Emit event with the specific vibration pattern */
    suspend fun makeVibration(event: VibrationEvent = VibrationEvent(pattern = VALIDATION_FAILED_VIBRATION)) {
        _events.emit(event)
    }

    /** Make vibration with the specified pattern */
    fun makeVibration(context: Context, event: VibrationEvent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrator = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                    as VibratorManager).defaultVibrator

            if (vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createWaveform(event.pattern, event.repeat))
            }
        }
    }
}