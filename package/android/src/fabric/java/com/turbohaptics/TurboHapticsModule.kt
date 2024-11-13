package com.turbohaptics

import androidx.annotation.NonNull
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.NativeModule

class TurboHapticsModule(context: ReactApplicationContext) :
    NativeTurboHapticsSpec(context),
    NativeModule {

    companion object {
        const val NAME = "TurboHaptics"

        init {
            System.loadLibrary("turbo-haptics")
        }
    }

    override fun getName(): String = NAME

    override fun initialize() {}

    override fun invalidate() {}

    override fun install(): Boolean {
        try {
            val catalystInstance = reactApplicationContext.catalystInstance
                ?: return false

            val runtimePointer = catalystInstance.javaScriptContextHolder.get()
            if (runtimePointer == 0L) {
                return false
            }

            return nativeInstallHaptics(runtimePointer, reactApplicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private external fun nativeInstallHaptics(runtimePointer: Long, context: ReactApplicationContext): Boolean
}