//
//  TurboHapticsModule.mm
//
//  Created by Christian Baroni on 11/10/24.
//  Copyright © 2024 Christian Baroni. All rights reserved.
//

#import "TurboHapticsModule.h"
#import "TurboHapticsBindings.h"

#ifdef RCT_NEW_ARCH_ENABLED
#import <ReactCommon/CallInvoker.h>
#import <ReactCommon/RCTTurboModuleWithJSIBindings.h>
#import <cxxreact/ReactNativeVersion.h>
#else
#import <React/RCTBridge+Private.h>
#endif

#include <memory>

#ifdef RCT_NEW_ARCH_ENABLED
@interface TurboHapticsModule () <RCTTurboModuleWithJSIBindings>
@end
#endif

@implementation TurboHapticsModule

RCT_EXPORT_MODULE(TurboHaptics)

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
        [center addObserver:self
                  selector:@selector(handleAppStateChange:)
                      name:UIApplicationWillResignActiveNotification
                    object:nil];
        [center addObserver:self
                  selector:@selector(handleAppStateChange:)
                      name:UIApplicationDidEnterBackgroundNotification
                    object:nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)handleAppStateChange:(NSNotification*)notification {
    (void)notification;
    turbohaptics::cleanup();
}

- (void)invalidate {
    turbohaptics::cleanup();
}

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams&)params {
    return std::make_shared<facebook::react::NativeTurboHapticsSpecJSI>(params);
}

#if defined(REACT_NATIVE_VERSION_MAJOR) && \
    defined(REACT_NATIVE_VERSION_MINOR) && \
    (REACT_NATIVE_VERSION_MAJOR > 0 || REACT_NATIVE_VERSION_MINOR >= 79)
- (void)installJSIBindingsWithRuntime:(facebook::jsi::Runtime&)runtime
                          callInvoker:(const std::shared_ptr<facebook::react::CallInvoker>&)callInvoker {
    (void)callInvoker;
    turbohaptics::installHapticFeedback(runtime);
}
#else
// RN 0.75-0.78 have no version macros; keep their compatible one-argument
// selector until support for that range is removed.
- (void)installJSIBindingsWithRuntime:(facebook::jsi::Runtime&)runtime {
    turbohaptics::installHapticFeedback(runtime);
}
#endif
#endif

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(install) {
#ifdef RCT_NEW_ARCH_ENABLED
    return @true;
#else
    RCTCxxBridge* cxxBridge = (RCTCxxBridge*)[RCTBridge currentBridge];
    if (cxxBridge == nil) {
        return @false;
    }

    auto* runtime = static_cast<facebook::jsi::Runtime*>(cxxBridge.runtime);
    if (runtime == nullptr) {
        return @false;
    }

    turbohaptics::installHapticFeedback(*runtime);
    return @true;
#endif
}

@end
