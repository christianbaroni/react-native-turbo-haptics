package com.turbohaptics

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

    private val haptics = HapticFeedback(context)

    override fun getName(): String = NAME

    override fun install(): Boolean = true

    override fun getBindingsInstaller(): BindingsInstallerHolder =
        nativeGetBindingsInstaller(haptics)

    override fun invalidate() {
        haptics.invalidate()
        super.invalidate()
    }

    private external fun nativeGetBindingsInstaller(haptics: HapticFeedback): BindingsInstallerHolder
}
