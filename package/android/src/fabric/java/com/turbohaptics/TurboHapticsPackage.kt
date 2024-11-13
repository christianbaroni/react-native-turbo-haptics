package com.turbohaptics

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class TurboHapticsPackage : TurboReactPackage() {
    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
        if (name == TurboHapticsModule.NAME) {
            return TurboHapticsModule(reactContext)
        }
        return null
    }

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
        return ReactModuleInfoProvider {
            val moduleInfos = HashMap<String, ReactModuleInfo>()
            val isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
            moduleInfos.apply {
                put(
                    TurboHapticsModule.NAME,
                    ReactModuleInfo(
                        TurboHapticsModule.NAME,
                        TurboHapticsModule.NAME,
                        false, // canOverrideExistingModule
                        false, // needsEagerInit
                        true,  // hasConstants
                        false, // isCxxModule
                        isTurboModule // isTurboModule
                    )
                )
            }
            moduleInfos
        }
    }
}