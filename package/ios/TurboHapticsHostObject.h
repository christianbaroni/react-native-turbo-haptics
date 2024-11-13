//
//  TurboHapticsHostObject.h
//
//  Created by Christian Baroni on 11/10/24.
//  Copyright © 2024 Christian Baroni. All rights reserved.
//

#pragma once

#include <jsi/jsi.h>

#ifdef __OBJC__
@class UIImpactFeedbackGenerator;
@class UINotificationFeedbackGenerator;
@class UISelectionFeedbackGenerator;
@class NSMutableDictionary;
#else
class UIImpactFeedbackGenerator;
class UINotificationFeedbackGenerator;
class UISelectionFeedbackGenerator;
class NSMutableDictionary;
#endif

using namespace facebook::jsi;

class JSI_EXPORT TurboHapticsHostObject : public facebook::jsi::HostObject {
public:
    TurboHapticsHostObject();
    ~TurboHapticsHostObject();
    
    facebook::jsi::Value get(facebook::jsi::Runtime& runtime, const facebook::jsi::PropNameID& name) override;
    void trigger(const std::string& type);
    static void cleanup();

private:
    static NSMutableDictionary* impactGeneratorMap;
    static UISelectionFeedbackGenerator* selectionGenerator;
    static UINotificationFeedbackGenerator* notificationGenerator;
};
