//
//  TurboHapticsModule.mm
//
//  Created by Christian Baroni on 11/10/24.
//  Copyright © 2024 Christian Baroni. All rights reserved.
//

#import "TurboHapticsModule.h"
#import "TurboHapticsHostObject.h"
#import <React/RCTBridge+Private.h>
#import <jsi/jsi.h>

@implementation TurboHapticsModule {
    facebook::jsi::Runtime* _runtime;
    std::shared_ptr<facebook::jsi::Function> _createHapticFeedback;
}

RCT_EXPORT_MODULE(TurboHaptics)

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

- (instancetype)init {
    if (self = [super init]) {
        NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
        [center addObserver:self
                  selector:@selector(handleAppWillResignActive:)
                      name:UIApplicationWillResignActiveNotification
                    object:nil];
        [center addObserver:self
                  selector:@selector(handleAppDidEnterBackground:)
                      name:UIApplicationDidEnterBackgroundNotification
                    object:nil];
        [center addObserver:self
                  selector:@selector(handleAppWillEnterForeground:)
                      name:UIApplicationWillEnterForegroundNotification
                    object:nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)handleAppWillResignActive:(NSNotification *)notification {
    TurboHapticsHostObject::cleanup();
}

- (void)handleAppDidEnterBackground:(NSNotification *)notification {
    TurboHapticsHostObject::cleanup();
}

- (void)handleAppWillEnterForeground:(NSNotification *)notification {
    TurboHapticsHostObject::cleanup();
}

- (void)invalidate {
    TurboHapticsHostObject::cleanup();
    _runtime = nil;
    _createHapticFeedback.reset();
}

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
   (const facebook::react::ObjCTurboModule::InitParams&)params {
    return std::make_shared<facebook::react::NativeTurboHapticsSpecJSI>(params);
}
#endif

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(install) {
    RCTBridge* bridge = [RCTBridge currentBridge];
    RCTCxxBridge* cxxBridge = (RCTCxxBridge*)bridge;
    
    if (cxxBridge == nil) {
        return @false;
    }

    auto jsiRuntime = (facebook::jsi::Runtime*)cxxBridge.runtime;
    if (jsiRuntime == nil) {
        return @false;
    }
    
    _runtime = jsiRuntime;
    auto& runtime = *jsiRuntime;
    
    _createHapticFeedback = std::make_shared<facebook::jsi::Function>(
        facebook::jsi::Function::createFromHostFunction(
            runtime,
            facebook::jsi::PropNameID::forAscii(runtime, "createHapticFeedback"),
            0,
            [](facebook::jsi::Runtime& runtime,
               const facebook::jsi::Value& thisValue,
               const facebook::jsi::Value* arguments,
               size_t count) -> facebook::jsi::Value {
                auto hostObject = std::make_shared<TurboHapticsHostObject>();
                return facebook::jsi::Object::createFromHostObject(runtime, hostObject);
            }));
        
    runtime.global().setProperty(runtime, "createHapticFeedback", *_createHapticFeedback);
    
    return @true;
}

@end
