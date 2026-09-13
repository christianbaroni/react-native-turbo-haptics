package com.turbohaptics

import android.os.Vibrator
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.turbomodule.core.interfaces.BindingsInstallerHolder
import com.facebook.react.turbomodule.core.interfaces.TurboModuleWithJSIBindings

class TurboHapticsModule(context: ReactApplicationContext) :
    NativeTurboHapticsSpec(context),
    TurboModuleWithJSIBindings {

    companion object {
        const val NAME = "TurboHaptics"

        init {
            System.loadLibrary("turbo-haptics")
        }
    }

    private val didInitialize = nativeInitialize(getDefaultVibrator(context))

    override fun getName(): String = NAME

    override fun install(): Boolean = didInitialize

    @DoNotStrip
    external override fun getBindingsInstaller(): BindingsInstallerHolder

    private external fun nativeInitialize(vibrator: Vibrator?): Boolean
}
