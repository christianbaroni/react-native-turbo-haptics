//
//  TurboHapticsBindings.mm
//
//  Created by Christian Baroni on 11/10/24.
//  Copyright © 2024 Christian Baroni. All rights reserved.
//

#import "TurboHapticsBindings.h"

#import <UIKit/UIKit.h>
#import <dispatch/dispatch.h>
#import <pthread.h>

#include <array>
#include <string>
#include <utility>

namespace turbohaptics {
namespace {
enum class HapticType {
    ImpactLight,
    ImpactMedium,
    ImpactHeavy,
    NotificationSuccess,
    NotificationWarning,
    NotificationError,
    Rigid,
    Selection,
    Soft,
    Invalid,
};

constexpr size_t kImpactGeneratorCount =
    static_cast<size_t>(UIImpactFeedbackStyleRigid) + 1;

std::array<UIImpactFeedbackGenerator*, kImpactGeneratorCount>
    impactGenerators;
UISelectionFeedbackGenerator* selectionGenerator = nil;
UINotificationFeedbackGenerator* notificationGenerator = nil;

HapticType decodeHapticType(const std::string& type) {
    if (type == "impactLight") {
        return HapticType::ImpactLight;
    }
    if (type == "impactMedium") {
        return HapticType::ImpactMedium;
    }
    if (type == "impactHeavy") {
        return HapticType::ImpactHeavy;
    }
    if (type == "notificationSuccess") {
        return HapticType::NotificationSuccess;
    }
    if (type == "notificationWarning") {
        return HapticType::NotificationWarning;
    }
    if (type == "notificationError") {
        return HapticType::NotificationError;
    }
    if (type == "rigid") {
        return HapticType::Rigid;
    }
    if (type == "selection") {
        return HapticType::Selection;
    }
    if (type == "soft") {
        return HapticType::Soft;
    }
    return HapticType::Invalid;
}

void clearGenerators() {
    impactGenerators.fill(nil);
    selectionGenerator = nil;
    notificationGenerator = nil;
}

void triggerImpact(UIImpactFeedbackStyle style) {
    auto& generator =
        impactGenerators[static_cast<size_t>(style)];
    if (generator == nil) {
        generator = [[UIImpactFeedbackGenerator alloc] initWithStyle:style];
    }

    [generator impactOccurred];
    [generator prepare];
}

void triggerNotification(UINotificationFeedbackType type) {
    if (notificationGenerator == nil) {
        notificationGenerator = [[UINotificationFeedbackGenerator alloc] init];
    }

    [notificationGenerator notificationOccurred:type];
    [notificationGenerator prepare];
}

void triggerSelection() {
    if (selectionGenerator == nil) {
        selectionGenerator = [[UISelectionFeedbackGenerator alloc] init];
    }

    [selectionGenerator selectionChanged];
    [selectionGenerator prepare];
}

void triggerOnMain(HapticType type) {
    switch (type) {
        case HapticType::ImpactLight:
            triggerImpact(UIImpactFeedbackStyleLight);
            return;
        case HapticType::ImpactMedium:
            triggerImpact(UIImpactFeedbackStyleMedium);
            return;
        case HapticType::ImpactHeavy:
            triggerImpact(UIImpactFeedbackStyleHeavy);
            return;
        case HapticType::NotificationSuccess:
            triggerNotification(UINotificationFeedbackTypeSuccess);
            return;
        case HapticType::NotificationWarning:
            triggerNotification(UINotificationFeedbackTypeWarning);
            return;
        case HapticType::NotificationError:
            triggerNotification(UINotificationFeedbackTypeError);
            return;
        case HapticType::Rigid:
            triggerImpact(UIImpactFeedbackStyleRigid);
            return;
        case HapticType::Selection:
            triggerSelection();
            return;
        case HapticType::Soft:
            triggerImpact(UIImpactFeedbackStyleSoft);
            return;
        case HapticType::Invalid:
            return;
    }
}

void trigger(HapticType type) {
    if (pthread_main_np() != 0) {
        triggerOnMain(type);
        return;
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        triggerOnMain(type);
    });
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
            [](facebook::jsi::Runtime& runtime,
               const facebook::jsi::Value&,
               const facebook::jsi::Value* arguments,
               size_t count) -> facebook::jsi::Value {
                if (count > 0 && arguments[0].isString()) {
                    const HapticType type = decodeHapticType(
                        arguments[0].asString(runtime).utf8(runtime));
                    if (type != HapticType::Invalid) {
                        trigger(type);
                    }
                }
                return facebook::jsi::Value::undefined();
            }));
    return haptics;
}
} // namespace

void cleanup() {
    if (pthread_main_np() != 0) {
        clearGenerators();
        return;
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        clearGenerators();
    });
}

void installHapticFeedback(facebook::jsi::Runtime& runtime) {
    auto createHapticFeedback = facebook::jsi::Function::createFromHostFunction(
        runtime,
        facebook::jsi::PropNameID::forAscii(runtime, "createHapticFeedback"),
        0,
        [](facebook::jsi::Runtime& runtime,
           const facebook::jsi::Value&,
           const facebook::jsi::Value*,
           size_t) -> facebook::jsi::Value {
            return createHapticFeedbackObject(runtime);
        });

    runtime.global().setProperty(
        runtime, "createHapticFeedback", std::move(createHapticFeedback));
}
} // namespace turbohaptics
