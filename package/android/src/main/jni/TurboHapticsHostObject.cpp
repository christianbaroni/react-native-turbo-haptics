#include "TurboHapticsHostObject.h"
#include <android/log.h>
#include <android/api-level.h>

#define LOG_TAG "TurboHaptics"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// VibrationEffect presets (API 29+)
// See: https://developer.android.com/reference/android/os/VibrationEffect
#define EFFECT_TICK 2 // Lightest
#define EFFECT_CLICK 0 // Medium
#define EFFECT_HEAVY_CLICK 5 // Heaviest
#define EFFECT_DOUBLE_CLICK 1

namespace turbohaptics {
    // Initialize static members
    JavaVM* TurboHapticsHostObject::jvm_ = nullptr;
    jobject TurboHapticsHostObject::vibrator_ = nullptr;
    jmethodID TurboHapticsHostObject::vibrateEffectMethod_ = nullptr;
    jmethodID TurboHapticsHostObject::legacyVibrateMethod_ = nullptr;
    jclass TurboHapticsHostObject::vibrationEffectClass_ = nullptr;
    jmethodID TurboHapticsHostObject::createPredefinedMethod_ = nullptr;
    bool TurboHapticsHostObject::hasVibrator_ = false;
    int TurboHapticsHostObject::androidApiLevel_ = 0;
    bool TurboHapticsHostObject::isInitialized_ = false;

    void TurboHapticsHostObject::initializeIfNeeded(JNIEnv* env, jobject context) {
        if (isInitialized_) return;

        // Store JavaVM pointer
        env->GetJavaVM(&jvm_);

        androidApiLevel_ = android_get_device_api_level();

        // Get vibrator service
        jstring serviceName = env->NewStringUTF("vibrator");
        jclass contextClass = env->GetObjectClass(context);
        jmethodID getSystemServiceMethod = env->GetMethodID(contextClass, "getSystemService", "(Ljava/lang/String;)Ljava/lang/Object;");

        jobject localVibrator = env->CallObjectMethod(context, getSystemServiceMethod, serviceName);
        env->DeleteLocalRef(serviceName);

        if (localVibrator) {
            vibrator_ = env->NewGlobalRef(localVibrator);
            jclass localVibratorClass = env->GetObjectClass(localVibrator);
            env->DeleteLocalRef(localVibrator);

            // Check vibrator capability
            jmethodID hasVibratorMethod = env->GetMethodID(localVibratorClass, "hasVibrator", "()Z");
            hasVibrator_ = env->CallBooleanMethod(vibrator_, hasVibratorMethod);

            // Cache methods based on API level
            if (androidApiLevel_ >= 26) { // VibrationEffect API
                jclass localEffectClass = env->FindClass("android/os/VibrationEffect");
                if (localEffectClass) {
                    vibrationEffectClass_ = (jclass)env->NewGlobalRef(localEffectClass);
                    createPredefinedMethod_ = env->GetStaticMethodID(vibrationEffectClass_, "createPredefined", "(I)Landroid/os/VibrationEffect;");
                    vibrateEffectMethod_ = env->GetMethodID(localVibratorClass, "vibrate", "(Landroid/os/VibrationEffect;)V");
                    env->DeleteLocalRef(localEffectClass);
                }
            } else {
                legacyVibrateMethod_ = env->GetMethodID(localVibratorClass, "vibrate", "(J)V");
            }
        }

        isInitialized_ = true;
    }

    void TurboHapticsHostObject::cleanup(JNIEnv* env) {
        if (vibrator_) {
            env->DeleteGlobalRef(vibrator_);
            vibrator_ = nullptr;
        }
        if (vibrationEffectClass_) {
            env->DeleteGlobalRef(vibrationEffectClass_);
            vibrationEffectClass_ = nullptr;
        }
        isInitialized_ = false;
    }

    TurboHapticsHostObject::TurboHapticsHostObject() = default;

    void TurboHapticsHostObject::trigger(const std::string& type) {
        if (!hasVibrator_ || !vibrator_ || !jvm_) return;

        JNIEnv* env;
        bool needsDetach = false;

        // Get current JNIEnv
        jint getEnvResult = jvm_->GetEnv((void**)&env, JNI_VERSION_1_6);
        if (getEnvResult == JNI_EDETACHED) {
            if (jvm_->AttachCurrentThread(&env, nullptr) == JNI_OK) {
                needsDetach = true;
            } else {
                return;
            }
        } else if (getEnvResult != JNI_OK) {
            return;
        }

        // Clear any existing exceptions before proceeding
        if (env->ExceptionCheck()) {
            env->ExceptionClear();
        }

        // Map feedback types to VibrationEffect constants
        // Default to lightest effect to handle unknown types
        int effect_id = EFFECT_TICK;

        if (type == "selection" || type == "soft") {
            effect_id = EFFECT_TICK;
        }
        else if (type == "impactLight" || type == "impactMedium" || type == "rigid") {
            effect_id = EFFECT_CLICK;
        }
        else if (type == "impactHeavy" || type == "notificationWarning" || type == "notificationError") {
            effect_id = EFFECT_HEAVY_CLICK;
        }
        else if (type == "notificationSuccess") {
            effect_id = EFFECT_DOUBLE_CLICK;
        }

        // Trigger vibration based on API level
        if (androidApiLevel_ >= 26 && vibrationEffectClass_ && createPredefinedMethod_ && vibrateEffectMethod_) {
            jobject effect = env->CallStaticObjectMethod(vibrationEffectClass_, createPredefinedMethod_, effect_id);
            if (!env->ExceptionCheck() && effect) {
                env->CallVoidMethod(vibrator_, vibrateEffectMethod_, effect);
                env->DeleteLocalRef(effect);
            }
        } else if (legacyVibrateMethod_) {
            env->CallVoidMethod(vibrator_, legacyVibrateMethod_, (jlong)50);
        }

        // Clear any exceptions that occurred during vibration
        if (env->ExceptionCheck()) {
            env->ExceptionClear();
        }

        // Detach if we attached
        if (needsDetach) {
            jvm_->DetachCurrentThread();
        }
    }

    facebook::jsi::Value TurboHapticsHostObject::get(
            facebook::jsi::Runtime& runtime,
            const facebook::jsi::PropNameID& name) {

        auto propertyName = name.utf8(runtime);

        if (propertyName == "trigger") {
            return facebook::jsi::Function::createFromHostFunction(
                    runtime,
                    name,
                    1,
                    [this](
                            facebook::jsi::Runtime& runtime,
                            const facebook::jsi::Value& thisValue,
                            const facebook::jsi::Value* arguments,
                            size_t count) -> facebook::jsi::Value {
                        if (count > 0 && arguments[0].isString()) {
                            std::string type = arguments[0].asString(runtime).utf8(runtime);
                            this->trigger(type);
                        }
                        return facebook::jsi::Value::undefined();
                    });
        }

        return facebook::jsi::Value::undefined();
    }

    void installHapticFeedback(facebook::jsi::Runtime& runtime, JNIEnv* env, jobject context) {
        // Initialize static vibrator state
        TurboHapticsHostObject::initializeIfNeeded(env, context);

        auto createHapticFeedback = facebook::jsi::Function::createFromHostFunction(
                runtime,
                facebook::jsi::PropNameID::forAscii(runtime, "createHapticFeedback"),
                0,
                [](
                        facebook::jsi::Runtime& runtime,
                        const facebook::jsi::Value& thisValue,
                        const facebook::jsi::Value* arguments,
                        size_t count) -> facebook::jsi::Value {
                    auto hostObject = std::make_shared<TurboHapticsHostObject>();
                    return facebook::jsi::Object::createFromHostObject(runtime, hostObject);
                });

        runtime.global().setProperty(runtime, "createHapticFeedback", std::move(createHapticFeedback));
    }
} // namespace turbohaptics

extern "C" {
JNIEXPORT jboolean JNICALL
Java_com_turbohaptics_TurboHapticsModule_nativeInstallHaptics(
        JNIEnv* env,
        jobject thiz,
        jlong runtimePointer,
        jobject context) {

    auto runtime = reinterpret_cast<facebook::jsi::Runtime*>(runtimePointer);
    if (!runtime) return JNI_FALSE;

    try {
        turbohaptics::installHapticFeedback(*runtime, env, context);
        return JNI_TRUE;
    } catch (const std::exception& e) {
        LOGE("Failed to install haptics: %s", e.what());
        return JNI_FALSE;
    }
}

// Cleanup when library is unloaded
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
        turbohaptics::TurboHapticsHostObject::cleanup(env);
    }
}
}