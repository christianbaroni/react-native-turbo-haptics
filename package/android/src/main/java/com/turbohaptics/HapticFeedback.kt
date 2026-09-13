package com.turbohaptics

import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.VibrationEffect.Composition.PRIMITIVE_CLICK
import android.os.VibrationEffect.Composition.PRIMITIVE_LOW_TICK
import android.os.VibrationEffect.Composition.PRIMITIVE_TICK
import android.os.VibratorManager
import android.provider.Settings
import android.view.HapticFeedbackConstants
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.common.LifecycleState
import java.lang.ref.WeakReference

internal class HapticFeedback(context: ReactApplicationContext) {
    private class Preset(val feedback: Int, val vibration: CombinedVibration? = null)
    private class Pulse(val primitive: Int, val scale: Float, val onsetMs: Int = 0)

    private val context = WeakReference(context)
    private val manager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.applicationContext.getSystemService(VibratorManager::class.java)
    } else {
        null
    }
    private val attributes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        VibrationAttributes.Builder().setUsage(VibrationAttributes.USAGE_TOUCH).build()
    } else {
        null
    }
    private val presets = mapOf(
        "selection" to Preset(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
            } else {
                HapticFeedbackConstants.CLOCK_TICK
            },
            compose(Pulse(PRIMITIVE_TICK, 0.7f)),
        ),
        "soft" to Preset(
            HapticFeedbackConstants.LONG_PRESS,
            compose(Pulse(PRIMITIVE_LOW_TICK, 1f)),
        ),
        "impactLight" to Preset(
            HapticFeedbackConstants.KEYBOARD_TAP,
            compose(Pulse(PRIMITIVE_CLICK, 0.5f)),
        ),
        "impactMedium" to Preset(
            HapticFeedbackConstants.VIRTUAL_KEY,
            compose(Pulse(PRIMITIVE_CLICK, 0.7f)),
        ),
        "impactHeavy" to Preset(
            HapticFeedbackConstants.LONG_PRESS,
            compose(Pulse(PRIMITIVE_CLICK, 1f)),
        ),
        "rigid" to Preset(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                HapticFeedbackConstants.CONTEXT_CLICK
            } else {
                HapticFeedbackConstants.CLOCK_TICK
            },
        ),
        "notificationSuccess" to Preset(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HapticFeedbackConstants.CONFIRM
            } else {
                HapticFeedbackConstants.VIRTUAL_KEY
            },
            compose(Pulse(PRIMITIVE_CLICK, 1f), Pulse(PRIMITIVE_TICK, 1f, 185)),
        ),
        "notificationWarning" to Preset(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HapticFeedbackConstants.REJECT
            } else {
                HapticFeedbackConstants.LONG_PRESS
            },
            compose(Pulse(PRIMITIVE_CLICK, 1f), Pulse(PRIMITIVE_CLICK, 1f, 215)),
        ),
        "notificationError" to Preset(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HapticFeedbackConstants.REJECT
            } else {
                HapticFeedbackConstants.LONG_PRESS
            },
            compose(
                Pulse(PRIMITIVE_TICK, 1f),
                Pulse(PRIMITIVE_TICK, 0.85f, 100),
                Pulse(PRIMITIVE_CLICK, 0.8f, 200),
                Pulse(PRIMITIVE_LOW_TICK, 1f, 300),
            ),
        ),
    )

    @DoNotStrip
    fun trigger(type: String) {
        val preset = presets[type] ?: return

        if (UiThreadUtil.isOnUiThread()) {
            perform(preset)
        } else {
            UiThreadUtil.runOnUiThread { perform(preset) }
        }
    }

    fun invalidate() {
        context.clear()
    }

    private fun compose(vararg pulses: Pulse): CombinedVibration? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return null
        }

        val vibrator = manager?.defaultVibrator ?: return null
        val durations = vibrator.getPrimitiveDurations(*IntArray(pulses.size) { pulses[it].primitive })
        val composition = VibrationEffect.startComposition()
        var previousEndMs = 0

        for ((index, pulse) in pulses.withIndex()) {
            val pauseMs = pulse.onsetMs - previousEndMs
            if (durations[index] <= 0 || pauseMs < 0) {
                return null
            }

            composition.addPrimitive(pulse.primitive, pulse.scale, pauseMs)
            previousEndMs = pulse.onsetMs + durations[index]
        }

        return CombinedVibration.createParallel(composition.compose())
    }

    private fun perform(preset: Preset) {
        val context = context.get() ?: return
        if (context.lifecycleState != LifecycleState.RESUMED) {
            return
        }

        val view = context.currentActivity?.window?.decorView ?: return
        if (!view.isAttachedToWindow || !view.isHapticFeedbackEnabled) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && preset.vibration != null) {
            // Android 13+ enforces this setting for USAGE_TOUCH in the vibration service.
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.HAPTIC_FEEDBACK_ENABLED,
                    0,
                ) == 0
            ) {
                return
            }

            manager?.vibrate(preset.vibration, attributes)
        } else {
            view.performHapticFeedback(preset.feedback)
        }
    }
}
