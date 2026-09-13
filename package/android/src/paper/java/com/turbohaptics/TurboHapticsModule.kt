package com.turbohaptics

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = TurboHapticsModule.NAME)
class TurboHapticsModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
    companion object {
        const val NAME = "TurboHaptics"

        init {
            System.loadLibrary("turbo-haptics")
        }
    }

    private val haptics = HapticFeedback(context)

    override fun getName(): String = NAME

    @ReactMethod(isBlockingSynchronousMethod = true)
    fun install(): Boolean {
        try {
            val catalystInstance = reactApplicationContext.catalystInstance ?: return false
            val runtimePointer = catalystInstance.javaScriptContextHolder.get()
            if (runtimePointer == 0L) {
                return false
            }

            return nativeInstallHaptics(runtimePointer, haptics)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun invalidate() {
        haptics.invalidate()
        super.invalidate()
    }

    private external fun nativeInstallHaptics(runtimePointer: Long, haptics: HapticFeedback): Boolean
}
