package com.turbohaptics

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager

@Suppress("DEPRECATION")
internal fun getDefaultVibrator(context: Context): Vibrator? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
