package com.turbohaptics

import android.app.Activity
import android.os.CombinedVibration
import android.os.Looper
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.VibrationEffect.Composition.PRIMITIVE_CLICK
import android.os.VibrationEffect.Composition.PRIMITIVE_LOW_TICK
import android.os.VibrationEffect.Composition.PRIMITIVE_TICK
import android.os.VibratorManager
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.ViewGroup
import com.facebook.react.bridge.ReactApplicationContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.LooperMode
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowLooper.shadowMainLooper
import org.robolectric.shadows.ShadowVibrator
import org.robolectric.shadows.ShadowViewGroup
import org.robolectric.util.ReflectionHelpers.callInstanceMethod
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [30],
    manifest = Config.NONE,
    shadows = [HapticFeedbackTest.RecordingView::class, HapticFeedbackTest.RecordingVibratorManager::class],
)
@LooperMode(LooperMode.Mode.PAUSED)
class HapticFeedbackTest {
    private lateinit var context: ReactApplicationContext
    private lateinit var activity: Activity
    private lateinit var haptics: HapticFeedback

    @Before
    @Suppress("DEPRECATION")
    fun setUp() {
        context = ReactApplicationContext(RuntimeEnvironment.getApplication())
        if (RuntimeEnvironment.getApiLevel() in 31..32) {
            Settings.System.putInt(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 1)
        }
        activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        context.onHostResume(activity)
        haptics = HapticFeedback(context)
    }

    @Test
    @Config(sdk = [21, 23, 29, 30, 31, 34])
    fun fallsBackToPlatformFeedbackWithoutSupportedPrimitives() {
        val expected = linkedMapOf(
            "selection" to if (RuntimeEnvironment.getApiLevel() >= 34) {
                HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
            } else {
                HapticFeedbackConstants.CLOCK_TICK
            },
            "soft" to HapticFeedbackConstants.LONG_PRESS,
            "impactLight" to HapticFeedbackConstants.KEYBOARD_TAP,
            "impactMedium" to HapticFeedbackConstants.VIRTUAL_KEY,
            "rigid" to if (RuntimeEnvironment.getApiLevel() >= 23) {
                HapticFeedbackConstants.CONTEXT_CLICK
            } else {
                HapticFeedbackConstants.CLOCK_TICK
            },
            "impactHeavy" to HapticFeedbackConstants.LONG_PRESS,
            "notificationWarning" to if (RuntimeEnvironment.getApiLevel() >= 30) {
                HapticFeedbackConstants.REJECT
            } else {
                HapticFeedbackConstants.LONG_PRESS
            },
            "notificationSuccess" to if (RuntimeEnvironment.getApiLevel() >= 30) {
                HapticFeedbackConstants.CONFIRM
            } else {
                HapticFeedbackConstants.VIRTUAL_KEY
            },
            "notificationError" to if (RuntimeEnvironment.getApiLevel() >= 30) {
                HapticFeedbackConstants.REJECT
            } else {
                HapticFeedbackConstants.LONG_PRESS
            },
        )

        expected.entries.forEachIndexed { index, (type, constant) ->
            haptics.trigger(type)
            assertEquals(type, index + 1, feedback(activity).size)
            assertEquals(type, constant, feedback(activity).last())
        }

        shadowMainLooper().idle()
        assertEquals(expected.values.toList(), feedback(activity))
        assertEquals(0, vibrationCount())
    }

    @Test
    @Config(sdk = [31, 34])
    fun playsDistinctCompositionsImmediatelyWithTouchUsage() {
        usePrimitiveDurations()
        val expected = linkedMapOf(
            "selection" to VibrationEffect.startComposition()
                .addPrimitive(PRIMITIVE_TICK, 0.7f).compose(),
            "soft" to VibrationEffect.startComposition()
                .addPrimitive(PRIMITIVE_LOW_TICK, 1f).compose(),
            "impactLight" to VibrationEffect.startComposition()
                .addPrimitive(PRIMITIVE_CLICK, 0.5f).compose(),
            "impactMedium" to VibrationEffect.startComposition()
                .addPrimitive(PRIMITIVE_CLICK, 0.7f).compose(),
            "impactHeavy" to VibrationEffect.startComposition()
                .addPrimitive(PRIMITIVE_CLICK, 1f).compose(),
            "notificationSuccess" to VibrationEffect.startComposition()
                .addPrimitive(PRIMITIVE_CLICK, 1f)
                .addPrimitive(PRIMITIVE_TICK, 1f, 174).compose(),
            "notificationWarning" to VibrationEffect.startComposition()
                .addPrimitive(PRIMITIVE_CLICK, 1f)
                .addPrimitive(PRIMITIVE_CLICK, 1f, 204).compose(),
            "notificationError" to VibrationEffect.startComposition()
                .addPrimitive(PRIMITIVE_TICK, 1f)
                .addPrimitive(PRIMITIVE_TICK, 0.85f, 95)
                .addPrimitive(PRIMITIVE_CLICK, 0.8f, 95)
                .addPrimitive(PRIMITIVE_LOW_TICK, 1f, 89).compose(),
        )

        for ((type, effect) in expected) {
            haptics.trigger(type)
            val recorded = recordingManager().vibrations.last()
            assertEquals(type, CombinedVibration.createParallel(effect), recorded.effect)
            assertEquals(VibrationAttributes.USAGE_TOUCH, recorded.attributes.usage)
            assertEquals(0, recorded.attributes.flags)
        }

        shadowMainLooper().idle()
        assertEquals(expected.size, vibrationCount())
        assertTrue(feedback(activity).isEmpty())
        haptics.trigger("rigid")
        assertEquals(listOf(HapticFeedbackConstants.CONTEXT_CLICK), feedback(activity))
    }

    @Test
    @Config(sdk = [31, 34])
    fun missingLowTickOnlyDisablesSoftAndErrorCompositions() {
        usePrimitiveDurations(lowTick = 0)

        haptics.trigger("soft")
        haptics.trigger("notificationError")
        haptics.trigger("selection")
        haptics.trigger("notificationSuccess")
        haptics.trigger("notificationWarning")
        haptics.trigger("impactLight")
        haptics.trigger("impactMedium")
        haptics.trigger("impactHeavy")

        assertEquals(
            listOf(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.REJECT),
            feedback(activity),
        )
        assertEquals(6, vibrationCount())
    }

    @Test
    @Config(sdk = [31, 34])
    fun missingTickOnlyDisablesSelectionSuccessAndErrorCompositions() {
        usePrimitiveDurations(tick = 0)

        haptics.trigger("selection")
        haptics.trigger("notificationSuccess")
        haptics.trigger("notificationError")
        haptics.trigger("soft")
        haptics.trigger("notificationWarning")
        haptics.trigger("impactLight")
        haptics.trigger("impactMedium")
        haptics.trigger("impactHeavy")

        assertEquals(
            listOf(
                if (RuntimeEnvironment.getApiLevel() >= 34) {
                    HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                },
                HapticFeedbackConstants.CONFIRM,
                HapticFeedbackConstants.REJECT,
            ),
            feedback(activity),
        )
        assertEquals(5, vibrationCount())
    }

    @Test
    @Config(sdk = [31, 34])
    fun notificationCadenceIsIndependentOfPrimitiveDuration() {
        usePrimitiveDurations(click = 25, tick = 8, lowTick = 15)
        val expected = mapOf(
            "notificationSuccess" to listOf(0, 185),
            "notificationWarning" to listOf(0, 215),
            "notificationError" to listOf(0, 100, 200, 300),
        )

        for ((type, onsets) in expected) {
            haptics.trigger(type)
            assertEquals(type, onsets, onsetTimes(recordingManager().vibrations.last().effect))
        }

        assertEquals(expected.size, vibrationCount())
        assertTrue(feedback(activity).isEmpty())
    }

    @Test
    @Config(sdk = [31, 34])
    fun fallsBackWhenPrimitiveDurationCannotFitTheRequestedCadence() {
        usePrimitiveDurations(click = 215)
        haptics.trigger("notificationWarning")
        assertEquals(listOf(0, 215), onsetTimes(recordingManager().vibrations.single().effect))

        usePrimitiveDurations(click = 216)
        haptics.trigger("notificationWarning")
        haptics.trigger("notificationSuccess")
        haptics.trigger("notificationError")
        haptics.trigger("impactHeavy")
        assertEquals(2, vibrationCount())
        assertEquals(
            listOf(
                HapticFeedbackConstants.REJECT,
                HapticFeedbackConstants.CONFIRM,
                HapticFeedbackConstants.REJECT,
            ),
            feedback(activity),
        )
    }

    @Test
    @Config(sdk = [31, 32])
    @Suppress("DEPRECATION")
    fun respectsChangesToTheLegacyTouchFeedbackSetting() {
        usePrimitiveDurations()
        Settings.System.putInt(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 0)
        haptics.trigger("selection")
        triggerFromBackground("selection")
        shadowMainLooper().idle()
        assertNoFeedback()

        Settings.System.putInt(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 1)
        haptics.trigger("selection")
        assertEquals(1, vibrationCount())
    }

    @Test
    @Config(sdk = [31, 32])
    @Suppress("DEPRECATION")
    fun absentLegacyTouchFeedbackSettingDisablesCompositions() {
        usePrimitiveDurations()
        Settings.System.putString(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, null)
        haptics.trigger("selection")
        triggerFromBackground("selection")

        shadowMainLooper().idle()
        assertNoFeedback()
    }

    @Test
    @Config(sdk = [21, 30, 31, 34])
    fun ignoresUnknownTypesFromEitherThread() {
        usePrimitiveDurations()
        for (type in listOf("", "unsupported", "Selection", "selection\u0000", "sélection")) {
            haptics.trigger(type)
            triggerFromBackground(type)
        }

        shadowMainLooper().idle()
        assertNoFeedback()
    }

    @Test
    @Config(sdk = [30, 31, 34])
    fun dispatchesBackgroundCallsExactlyOnceOnMain() {
        usePrimitiveDurations()
        triggerFromBackground("selection")
        assertNoFeedback()

        shadowMainLooper().idle()
        assertEquals(1, feedback(activity).size + vibrationCount())

        shadowMainLooper().idle()
        assertEquals(1, feedback(activity).size + vibrationCount())
    }

    @Test
    @Config(sdk = [30, 31, 34])
    fun invalidationDiscardsPendingAndFutureCalls() {
        usePrimitiveDurations()
        triggerFromBackground("selection")
        haptics.invalidate()

        shadowMainLooper().idle()
        haptics.trigger("selection")
        assertNoFeedback()
    }

    @Test
    fun resolvesTheCurrentActivityWhenTheQueueDrains() {
        val replacement = Robolectric.buildActivity(Activity::class.java).setup().get()
        triggerFromBackground("selection")
        context.onHostPause()
        context.onHostResume(replacement)

        shadowMainLooper().idle()
        assertTrue(feedback(activity).isEmpty())
        assertEquals(listOf(HapticFeedbackConstants.CLOCK_TICK), feedback(replacement))
    }

    @Test
    @Config(sdk = [31, 34])
    fun queuedCompositionsUseTheCurrentViewsHapticSetting() {
        usePrimitiveDurations()
        val replacement = Robolectric.buildActivity(Activity::class.java).setup().get()
        replacement.window.decorView.isHapticFeedbackEnabled = false
        triggerFromBackground("selection")
        context.onHostPause()
        context.onHostResume(replacement)

        shadowMainLooper().idle()
        assertNoFeedback()
        assertTrue(feedback(replacement).isEmpty())
    }

    @Test
    @Config(sdk = [30, 31, 34])
    fun detachedWindowsAreInert() {
        usePrimitiveDurations()
        val detached = Robolectric.buildActivity(Activity::class.java).create().get()
        context.onHostResume(detached)
        assertFalse(detached.window.decorView.isAttachedToWindow)

        haptics.trigger("selection")
        triggerFromBackground("selection")
        shadowMainLooper().idle()
        assertNoFeedback()
        assertTrue(feedback(detached).isEmpty())
    }

    @Test
    @Config(sdk = [30, 31, 34])
    fun pausedContextsDiscardPendingAndImmediateCalls() {
        usePrimitiveDurations()
        triggerFromBackground("selection")
        context.onHostPause()
        haptics.trigger("selection")

        shadowMainLooper().idle()
        assertNoFeedback()
    }

    @Test
    @Config(sdk = [30, 31, 34])
    fun resumedContextsWithoutAnActivityAreInert() {
        usePrimitiveDurations()
        context.onHostResume(null)
        haptics.trigger("selection")
        triggerFromBackground("selection")

        shadowMainLooper().idle()
        assertNoFeedback()
    }

    private fun usePrimitiveDurations(click: Int = 11, tick: Int = 5, lowTick: Int = 12) {
        if (RuntimeEnvironment.getApiLevel() >= 31) {
            val manager = context.getSystemService(VibratorManager::class.java)
            val vibrator = Shadow.extract<ShadowVibrator>(manager.defaultVibrator)
            vibrator.setPrimitiveDurations(PRIMITIVE_CLICK, click)
            vibrator.setPrimitiveDurations(PRIMITIVE_TICK, tick)
            vibrator.setPrimitiveDurations(PRIMITIVE_LOW_TICK, lowTick)
            haptics = HapticFeedback(context)
        }
    }

    private fun onsetTimes(vibration: CombinedVibration): List<Int> {
        val effect = callInstanceMethod<VibrationEffect>(vibration, "getEffect")
        val segments = callInstanceMethod<List<Any>>(effect, "getSegments")
        val vibrator = context.getSystemService(VibratorManager::class.java).defaultVibrator
        var elapsedMs = 0

        return segments.map { segment ->
            val primitive = callInstanceMethod<Int>(segment, "getPrimitiveId")
            val onsetMs = elapsedMs + callInstanceMethod<Int>(segment, "getDelay")
            elapsedMs = onsetMs + vibrator.getPrimitiveDurations(primitive).single()
            onsetMs
        }
    }

    private fun triggerFromBackground(type: String) {
        val call = FutureTask { haptics.trigger(type) }
        Thread(call, "haptics-test").apply {
            isDaemon = true
            start()
        }
        call.get(5, TimeUnit.SECONDS)
    }

    private fun feedback(activity: Activity): List<Int> =
        Shadow.extract<RecordingView>(activity.window.decorView).feedback

    private fun recordingManager(): RecordingVibratorManager =
        Shadow.extract(context.getSystemService(VibratorManager::class.java))

    private fun vibrationCount(): Int =
        if (RuntimeEnvironment.getApiLevel() >= 31) recordingManager().vibrations.size else 0

    private fun assertNoFeedback() {
        assertTrue(feedback(activity).isEmpty())
        assertEquals(0, vibrationCount())
    }

    @Implements(ViewGroup::class)
    class RecordingView : ShadowViewGroup() {
        val feedback = mutableListOf<Int>()

        @Implementation
        override fun performHapticFeedback(constant: Int): Boolean {
            assertSame(Looper.getMainLooper().thread, Thread.currentThread())
            feedback.add(constant)
            return true
        }
    }

    @Implements(VibratorManager::class, minSdk = 31)
    class RecordingVibratorManager {
        data class Vibration(val effect: CombinedVibration, val attributes: VibrationAttributes)

        val vibrations = mutableListOf<Vibration>()

        @Implementation
        fun vibrate(effect: CombinedVibration, attributes: VibrationAttributes) {
            assertSame(Looper.getMainLooper().thread, Thread.currentThread())
            vibrations.add(Vibration(effect, attributes))
        }
    }
}
