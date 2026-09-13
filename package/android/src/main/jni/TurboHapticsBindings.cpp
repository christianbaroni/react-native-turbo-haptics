#include <android/log.h>
#include <fbjni/fbjni.h>
#include <jsi/jsi.h>

#include <exception>
#include <memory>
#include <string>
#include <utility>

#ifdef TURBO_HAPTICS_NEW_ARCHITECTURE
#include <ReactCommon/BindingsInstallerHolder.h>
#include <cxxreact/ReactNativeVersion.h>
#endif

namespace turbohaptics {
namespace {
namespace jni = facebook::jni;
namespace jsi = facebook::jsi;

class HapticFeedback {
 public:
  explicit HapticFeedback(jni::alias_ref<jobject> instance)
      : instance_(jni::make_global(instance)),
        triggerMethod_(instance->getClass()->getMethod<void(jstring)>("trigger")) {}

  ~HapticFeedback() {
    jni::ThreadScope scope;
    instance_.reset();
  }

  void trigger(const std::string& type) const {
    // fbjni's UTF-8 conversion would truncate an embedded NUL into a valid name.
    if (type.find('\0') != std::string::npos) {
      return;
    }

    jni::ThreadScope scope;
    if (jni::Environment::current()->ExceptionCheck()) {
      return;
    }

    try {
      triggerMethod_(instance_, jni::make_jstring(type).get());
    } catch (const jni::JniException&) {
      // Optional feedback must not turn a platform failure into a JS error.
    }
  }

 private:
  jni::global_ref<jobject> instance_;
  jni::JMethod<void(jstring)> triggerMethod_;
};

jsi::Object createHapticFeedbackObject(
    jsi::Runtime& runtime,
    const std::shared_ptr<HapticFeedback>& haptics) {
  jsi::Object object(runtime);
  object.setProperty(
      runtime,
      "trigger",
      jsi::Function::createFromHostFunction(
          runtime,
          jsi::PropNameID::forAscii(runtime, "trigger"),
          1,
          [haptics](
              jsi::Runtime& runtime,
              const jsi::Value&,
              const jsi::Value* arguments,
              size_t count) -> jsi::Value {
            if (count > 0 && arguments[0].isString()) {
              haptics->trigger(arguments[0].asString(runtime).utf8(runtime));
            }
            return jsi::Value::undefined();
          }));
  return object;
}

void installHapticFeedback(
    jsi::Runtime& runtime,
    const std::shared_ptr<HapticFeedback>& haptics) {
  auto createHapticFeedback = jsi::Function::createFromHostFunction(
      runtime,
      jsi::PropNameID::forAscii(runtime, "createHapticFeedback"),
      0,
      [haptics](
          jsi::Runtime& runtime,
          const jsi::Value&,
          const jsi::Value*,
          size_t) -> jsi::Value {
        return createHapticFeedbackObject(runtime, haptics);
      });

  runtime.global().setProperty(
      runtime, "createHapticFeedback", std::move(createHapticFeedback));
}
} // namespace
} // namespace turbohaptics

extern "C" {
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return facebook::jni::initialize(vm, [] {});
}

#ifdef TURBO_HAPTICS_NEW_ARCHITECTURE
JNIEXPORT jobject JNICALL
Java_com_turbohaptics_TurboHapticsModule_nativeGetBindingsInstaller(
    JNIEnv*,
    jobject,
    jobject instance) {
  try {
    auto haptics = std::make_shared<turbohaptics::HapticFeedback>(
        facebook::jni::wrap_alias(instance));

    // RN 0.75-0.78 have no version macros; keep their compatible one-argument
    // callback until support for that range is removed.
#if defined(REACT_NATIVE_VERSION_MAJOR) && \
    defined(REACT_NATIVE_VERSION_MINOR) && \
    (REACT_NATIVE_VERSION_MAJOR > 0 || REACT_NATIVE_VERSION_MINOR >= 79)
    auto installer = [haptics](
        facebook::jsi::Runtime& runtime,
        const std::shared_ptr<facebook::react::CallInvoker>&) {
      turbohaptics::installHapticFeedback(runtime, haptics);
    };
#else
    auto installer = [haptics](facebook::jsi::Runtime& runtime) {
      turbohaptics::installHapticFeedback(runtime, haptics);
    };
#endif
    return facebook::react::BindingsInstallerHolder::newObjectCxxArgs(
               std::move(installer))
        .release();
  } catch (...) {
    facebook::jni::translatePendingCppExceptionToJavaException();
    return nullptr;
  }
}
#else
JNIEXPORT jboolean JNICALL
Java_com_turbohaptics_TurboHapticsModule_nativeInstallHaptics(
    JNIEnv*,
    jobject,
    jlong runtimePointer,
    jobject instance) {
  auto* runtime = reinterpret_cast<facebook::jsi::Runtime*>(runtimePointer);
  if (!runtime) {
    return JNI_FALSE;
  }

  try {
    auto haptics = std::make_shared<turbohaptics::HapticFeedback>(
        facebook::jni::wrap_alias(instance));
    turbohaptics::installHapticFeedback(*runtime, haptics);
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
}
