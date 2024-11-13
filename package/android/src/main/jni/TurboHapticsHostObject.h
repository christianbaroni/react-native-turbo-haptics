#pragma once

#include <jni.h>
#include <jsi/jsi.h>

namespace turbohaptics {
    class JSI_EXPORT TurboHapticsHostObject final : public facebook::jsi::HostObject {
        public:
            TurboHapticsHostObject();
            ~TurboHapticsHostObject() = default;

            facebook::jsi::Value get(facebook::jsi::Runtime& runtime, const facebook::jsi::PropNameID& name) override;

            static void cleanup(JNIEnv* env);

            // Static initialization
            static void initializeIfNeeded(JNIEnv* env, jobject context);

        private:
            void trigger(const std::string& type);

            // Static members
            static JavaVM* jvm_;
            static jobject vibrator_;
            static jmethodID vibrateEffectMethod_;
            static jmethodID legacyVibrateMethod_;
            static jclass vibrationEffectClass_;
            static jmethodID createPredefinedMethod_;
            static bool hasVibrator_;
            static int androidApiLevel_;
            static bool isInitialized_;

    };

    void installHapticFeedback(facebook::jsi::Runtime& runtime, JNIEnv* env, jobject context);
} // namespace turbohaptics