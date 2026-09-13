#include <android/api-level.h>
#include <android/log.h>
#include <jni.h>
#include <jsi/jsi.h>

#include <atomic>
#include <exception>
#include <memory>
#include <mutex>
#include <optional>
#include <string>
#include <utility>

#ifdef TURBO_HAPTICS_NEW_ARCHITECTURE
#include <ReactCommon/BindingsInstallerHolder.h>
#include <cxxreact/ReactNativeVersion.h>
#include <fbjni/fbjni.h>
#endif

namespace turbohaptics {
namespace {
void installHapticFeedback(facebook::jsi::Runtime& runtime);

enum class HapticEffect : jint {
  Click = 0,
  DoubleClick = 1,
  Tick = 2,
  HeavyClick = 5,
  Invalid = -1,
};

enum class VibrationMode {
  Duration,
  OneShot,
  Predefined,
};

constexpr jlong kFallbackDurationMs = 50;
constexpr jint kDefaultAmplitude = -1;

struct NativeState {
  JavaVM* vm = nullptr;
  jobject vibrator = nullptr;
  jclass effectClass = nullptr;
  jmethodID vibrateMethod = nullptr;
  jmethodID createEffectMethod = nullptr;
  VibrationMode mode = VibrationMode::Duration;
};

std::once_flag initializeOnce;
NativeState nativeState;
std::atomic<const NativeState*> publishedState{nullptr};

#ifdef TURBO_HAPTICS_NEW_ARCHITECTURE
class TurboHapticsModuleJSIBindings
    : public facebook::jni::JavaClass<TurboHapticsModuleJSIBindings> {
 public:
  static constexpr auto kJavaDescriptor =
      "Lcom/turbohaptics/TurboHapticsModule;";

  static void registerNatives();

 private:
  static facebook::jni::local_ref<
      facebook::react::BindingsInstallerHolder::javaobject>
  getBindingsInstaller(
      facebook::jni::alias_ref<TurboHapticsModuleJSIBindings>);
};

void TurboHapticsModuleJSIBindings::registerNatives() {
  javaClassLocal()->registerNatives({
      makeNativeMethod(
          "getBindingsInstaller",
          TurboHapticsModuleJSIBindings::getBindingsInstaller),
  });
}

facebook::jni::local_ref<
    facebook::react::BindingsInstallerHolder::javaobject>
TurboHapticsModuleJSIBindings::getBindingsInstaller(
    facebook::jni::alias_ref<TurboHapticsModuleJSIBindings>) {
  // RN 0.75-0.78 have no version macros; keep their compatible one-argument
  // callback until support for that range is removed.
#if defined(REACT_NATIVE_VERSION_MAJOR) && \
    defined(REACT_NATIVE_VERSION_MINOR) && \
    (REACT_NATIVE_VERSION_MAJOR > 0 || REACT_NATIVE_VERSION_MINOR >= 79)
  return facebook::react::BindingsInstallerHolder::newObjectCxxArgs(
      [](facebook::jsi::Runtime& runtime,
         const std::shared_ptr<facebook::react::CallInvoker>&) {
        installHapticFeedback(runtime);
      });
#else
  return facebook::react::BindingsInstallerHolder::newObjectCxxArgs(
      [](facebook::jsi::Runtime& runtime) {
        installHapticFeedback(runtime);
      });
#endif
}
#endif

bool clearJniException(JNIEnv* env) {
  if (!env->ExceptionCheck()) {
    return false;
  }

  env->ExceptionClear();
  return true;
}

std::optional<NativeState> createNativeState(
    JNIEnv* env,
    jobject vibratorObject) {
  if (!vibratorObject || env->ExceptionCheck()) {
    return std::nullopt;
  }

  JavaVM* vm = nullptr;
  if (env->GetJavaVM(&vm) != JNI_OK) {
    return std::nullopt;
  }

  jclass vibratorClass = env->GetObjectClass(vibratorObject);
  const bool vibratorClassFailed = clearJniException(env);
  if (!vibratorClass || vibratorClassFailed) {
    return std::nullopt;
  }

  jmethodID hasVibratorMethod =
      env->GetMethodID(vibratorClass, "hasVibrator", "()Z");
  const bool hasVibratorLookupFailed = clearJniException(env);
  if (!hasVibratorMethod || hasVibratorLookupFailed) {
    return std::nullopt;
  }

  const bool canVibrate =
      env->CallBooleanMethod(vibratorObject, hasVibratorMethod) == JNI_TRUE;
  if (clearJniException(env)) {
    return std::nullopt;
  }

  if (!canVibrate) {
    return NativeState{
        vm,
        nullptr,
        nullptr,
        nullptr,
        nullptr,
        VibrationMode::Duration,
    };
  }

  const int androidApiLevel = android_get_device_api_level();
  jclass localEffectClass = nullptr;
  jmethodID createEffectMethod = nullptr;
  jmethodID vibrateMethod = nullptr;
  VibrationMode mode = VibrationMode::Duration;

  if (androidApiLevel >= 26) {
    localEffectClass = env->FindClass("android/os/VibrationEffect");
    if (clearJniException(env) || !localEffectClass) {
      return std::nullopt;
    }

    if (androidApiLevel >= 29) {
      createEffectMethod = env->GetStaticMethodID(
          localEffectClass,
          "createPredefined",
          "(I)Landroid/os/VibrationEffect;");
      mode = VibrationMode::Predefined;
    } else {
      createEffectMethod = env->GetStaticMethodID(
          localEffectClass,
          "createOneShot",
          "(JI)Landroid/os/VibrationEffect;");
      mode = VibrationMode::OneShot;
    }
    if (clearJniException(env) || !createEffectMethod) {
      return std::nullopt;
    }

    vibrateMethod = env->GetMethodID(
        vibratorClass,
        "vibrate",
        "(Landroid/os/VibrationEffect;)V");
    if (clearJniException(env) || !vibrateMethod) {
      return std::nullopt;
    }
  } else {
    vibrateMethod = env->GetMethodID(vibratorClass, "vibrate", "(J)V");
    if (clearJniException(env) || !vibrateMethod) {
      return std::nullopt;
    }
  }

  jobject globalVibrator = env->NewGlobalRef(vibratorObject);
  const bool vibratorRefFailed = clearJniException(env);
  if (!globalVibrator || vibratorRefFailed) {
    if (globalVibrator) {
      env->DeleteGlobalRef(globalVibrator);
    }
    return std::nullopt;
  }

  jclass globalEffectClass = nullptr;
  if (localEffectClass) {
    globalEffectClass =
        static_cast<jclass>(env->NewGlobalRef(localEffectClass));
    const bool effectClassRefFailed = clearJniException(env);
    if (!globalEffectClass || effectClassRefFailed) {
      env->DeleteGlobalRef(globalVibrator);
      if (globalEffectClass) {
        env->DeleteGlobalRef(globalEffectClass);
      }
      return std::nullopt;
    }
  }

  return NativeState{
      vm,
      globalVibrator,
      globalEffectClass,
      vibrateMethod,
      createEffectMethod,
      mode,
  };
}

HapticEffect decodeHapticEffect(const std::string& type) {
  if (type == "selection" || type == "soft") {
    return HapticEffect::Tick;
  }
  if (
      type == "impactLight" || type == "impactMedium" || type == "rigid") {
    return HapticEffect::Click;
  }
  if (
      type == "impactHeavy" || type == "notificationWarning" ||
      type == "notificationError") {
    return HapticEffect::HeavyClick;
  }
  if (type == "notificationSuccess") {
    return HapticEffect::DoubleClick;
  }
  return HapticEffect::Invalid;
}

void trigger(const std::string& type) {
  const NativeState* state = publishedState.load(std::memory_order_acquire);
  if (!state || !state->vibrator) {
    return;
  }

  const HapticEffect effect = decodeHapticEffect(type);
  if (effect == HapticEffect::Invalid) {
    return;
  }

  JNIEnv* env = nullptr;
  bool needsDetach = false;

  const jint getEnvResult =
      state->vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
  if (getEnvResult == JNI_EDETACHED) {
    if (state->vm->AttachCurrentThread(&env, nullptr) == JNI_OK) {
      needsDetach = true;
    } else {
      return;
    }
  } else if (getEnvResult != JNI_OK) {
    return;
  }

  if (env->ExceptionCheck()) {
    return;
  }

  if (state->mode == VibrationMode::Duration) {
    env->CallVoidMethod(
        state->vibrator,
        state->vibrateMethod,
        kFallbackDurationMs);
  } else {
    jobject vibrationEffect = nullptr;
    if (state->mode == VibrationMode::Predefined) {
      vibrationEffect = env->CallStaticObjectMethod(
          state->effectClass,
          state->createEffectMethod,
          static_cast<jint>(effect));
    } else {
      vibrationEffect = env->CallStaticObjectMethod(
          state->effectClass,
          state->createEffectMethod,
          kFallbackDurationMs,
          kDefaultAmplitude);
    }

    if (!env->ExceptionCheck() && vibrationEffect) {
      env->CallVoidMethod(
          state->vibrator,
          state->vibrateMethod,
          vibrationEffect);
    }
    if (vibrationEffect) {
      env->DeleteLocalRef(vibrationEffect);
    }
  }

  if (env->ExceptionCheck()) {
    env->ExceptionClear();
  }

  if (needsDetach) {
    state->vm->DetachCurrentThread();
  }
}

facebook::jsi::Object createHapticFeedbackObject(
    facebook::jsi::Runtime& runtime) {
  facebook::jsi::Object haptics(runtime);
  haptics.setProperty(
      runtime,
      "trigger",
      facebook::jsi::Function::createFromHostFunction(
          runtime,
          facebook::jsi::PropNameID::forAscii(runtime, "trigger"),
          1,
          [](
              facebook::jsi::Runtime& runtime,
              const facebook::jsi::Value&,
              const facebook::jsi::Value* arguments,
              size_t count) -> facebook::jsi::Value {
            if (count > 0 && arguments[0].isString()) {
              trigger(arguments[0].asString(runtime).utf8(runtime));
            }
            return facebook::jsi::Value::undefined();
          }));
  return haptics;
}

bool initialize(JNIEnv* env, jobject vibrator) {
  if (!vibrator || env->ExceptionCheck()) {
    return false;
  }

  std::call_once(initializeOnce, [&] {
    auto state = createNativeState(env, vibrator);
    if (state) {
      nativeState = *state;
      publishedState.store(&nativeState, std::memory_order_release);
    }
  });
  return publishedState.load(std::memory_order_acquire) != nullptr;
}

void cleanup(JNIEnv* env) {
  const NativeState* state =
      publishedState.exchange(nullptr, std::memory_order_acq_rel);
  if (!state) {
    return;
  }

  if (state->vibrator) {
    env->DeleteGlobalRef(state->vibrator);
  }
  if (state->effectClass) {
    env->DeleteGlobalRef(state->effectClass);
  }
  nativeState = {};
}

void installHapticFeedback(facebook::jsi::Runtime& runtime) {
  auto createHapticFeedback = facebook::jsi::Function::createFromHostFunction(
      runtime,
      facebook::jsi::PropNameID::forAscii(runtime, "createHapticFeedback"),
      0,
      [](
          facebook::jsi::Runtime& runtime,
          const facebook::jsi::Value&,
          const facebook::jsi::Value*,
          size_t) -> facebook::jsi::Value {
        return createHapticFeedbackObject(runtime);
      });

  runtime.global().setProperty(
      runtime, "createHapticFeedback", std::move(createHapticFeedback));
}
} // namespace
} // namespace turbohaptics

extern "C" {
#ifdef TURBO_HAPTICS_NEW_ARCHITECTURE
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return facebook::jni::initialize(vm, [] {
    turbohaptics::TurboHapticsModuleJSIBindings::registerNatives();
  });
}

JNIEXPORT jboolean JNICALL
Java_com_turbohaptics_TurboHapticsModule_nativeInitialize(
    JNIEnv* env,
    jobject,
    jobject vibrator) {
  return turbohaptics::initialize(env, vibrator) ? JNI_TRUE : JNI_FALSE;
}
#else
JNIEXPORT jboolean JNICALL
Java_com_turbohaptics_TurboHapticsModule_nativeInstallHaptics(
    JNIEnv* env,
    jobject,
    jlong runtimePointer,
    jobject vibrator) {
  auto* runtime = reinterpret_cast<facebook::jsi::Runtime*>(runtimePointer);
  if (!runtime) {
    return JNI_FALSE;
  }

  try {
    if (!turbohaptics::initialize(env, vibrator)) {
      return JNI_FALSE;
    }
    turbohaptics::installHapticFeedback(*runtime);
    return JNI_TRUE;
  } catch (const std::exception& error) {
    __android_log_print(
        ANDROID_LOG_ERROR,
        "TurboHaptics",
        "Failed to install haptics: %s",
        error.what());
    return JNI_FALSE;
  }
}
#endif

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void*) {
  JNIEnv* env = nullptr;
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) == JNI_OK) {
    turbohaptics::cleanup(env);
  }
}
}
