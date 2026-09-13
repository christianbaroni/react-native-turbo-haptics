package com.turbohaptics

import android.os.Vibrator
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = TurboHapticsModule.NAME)
class TurboHapticsModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    companion object {
        const val NAME = "TurboHaptics"

        init {
            System.loadLibrary("turbo-haptics")
        }
    }

    override fun getName(): String = NAME

    @ReactMethod(isBlockingSynchronousMethod = true)
    fun install(): Boolean {
        try {
            val catalystInstance = reactApplicationContext.catalystInstance
                    ?: return false

            val runtimePointer = catalystInstance.javaScriptContextHolder.get()
            if (runtimePointer == 0L) {
                return false
            }

            return nativeInstallHaptics(runtimePointer, getDefaultVibrator(reactApplicationContext))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private external fun nativeInstallHaptics(runtimePointer: Long, vibrator: Vibrator?): Boolean
}
