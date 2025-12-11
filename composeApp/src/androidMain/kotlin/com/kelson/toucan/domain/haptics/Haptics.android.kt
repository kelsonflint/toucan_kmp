package com.kelson.toucan.domain.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidHaptics(context: Context) : Haptics {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun success() {
        vibrate(VibrationEffect.EFFECT_HEAVY_CLICK)
    }

    override fun error() {
        vibrate(VibrationEffect.EFFECT_DOUBLE_CLICK)
    }

    override fun light() {
        vibrate(VibrationEffect.EFFECT_TICK)
    }

    private fun vibrate(effectId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(effectId))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }
}

@Composable
actual fun rememberHaptics(): Haptics {
    val context = LocalContext.current
    return remember { AndroidHaptics(context) }
}
