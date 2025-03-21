package com.example.workouttracker.ui.managers

import com.example.workouttracker.utils.Constants.VALIDATION_FAILED_VIBRATION
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/** Vibration event class */
data class VibrationEvent(
    val pattern: LongArray,
    val repeat: Int = -1
)

/** Class to handle vibrations logic to warn the user when needed */
object VibrationManager {

    private val _events = Channel<VibrationEvent>()
    val events = _events.receiveAsFlow()

    /** Make vibration with the specified pattern */
    suspend fun makeVibration(event: VibrationEvent = VibrationEvent(pattern = VALIDATION_FAILED_VIBRATION)) {
        _events.send(event)
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