//
//  TurboHapticsHostObject.mm
//
//  Created by Christian Baroni on 11/10/24.
//  Copyright © 2024 Christian Baroni. All rights reserved.
//

#import "TurboHapticsHostObject.h"
#import <UIKit/UIKit.h>
#import <jsi/jsi.h>

NSMutableDictionary* TurboHapticsHostObject::impactGeneratorMap = nil;
UISelectionFeedbackGenerator* TurboHapticsHostObject::selectionGenerator = nil;
UINotificationFeedbackGenerator* TurboHapticsHostObject::notificationGenerator = nil;

TurboHapticsHostObject::TurboHapticsHostObject() = default;
TurboHapticsHostObject::~TurboHapticsHostObject() {
    cleanup();
}

void TurboHapticsHostObject::cleanup() {
    if (impactGeneratorMap != nil) {
        for (NSString* key in impactGeneratorMap) {
            UIImpactFeedbackGenerator* generator = impactGeneratorMap[key];
            if (generator != nil) {
                [generator prepare]; // Ensure any pending haptics are cleared
            }
        }
        [impactGeneratorMap removeAllObjects];
        impactGeneratorMap = nil;
    }
    
    if (selectionGenerator != nil) {
        [selectionGenerator prepare];
        selectionGenerator = nil;
    }
    
    if (notificationGenerator != nil) {
        [notificationGenerator prepare];
        notificationGenerator = nil;
    }
}

void TurboHapticsHostObject::trigger(const std::string& type) {
    if (type == "impactLight" || type == "impactMedium" || type == "impactHeavy" ||
        type == "soft" || type == "rigid") {
        
        UIImpactFeedbackStyle style = UIImpactFeedbackStyleMedium;
        if (type == "impactLight") style = UIImpactFeedbackStyleLight;
        else if (type == "impactHeavy") style = UIImpactFeedbackStyleHeavy;
        else if (type == "soft") style = UIImpactFeedbackStyleSoft;
        else if (type == "rigid") style = UIImpactFeedbackStyleRigid;
        
        NSString *key = [[NSNumber numberWithInteger: style] stringValue];
        if (impactGeneratorMap == nil) {
            impactGeneratorMap = [[NSMutableDictionary alloc] init];
        }
        
        UIImpactFeedbackGenerator *generator = [impactGeneratorMap objectForKey:key];
        if (generator == nil) {
            generator = [[UIImpactFeedbackGenerator alloc] initWithStyle:style];
            [impactGeneratorMap setValue:generator forKey:key];
            [generator prepare];
        }
        
        [generator impactOccurred];
        [generator prepare];
        
    } else if (type == "notificationSuccess" || type == "notificationWarning" ||
               type == "notificationError") {
        
        if (notificationGenerator == nil) {
            notificationGenerator = [[UINotificationFeedbackGenerator alloc] init];
            [notificationGenerator prepare];
        }
        
        UINotificationFeedbackType feedbackType = UINotificationFeedbackTypeSuccess;
        if (type == "notificationWarning") feedbackType = UINotificationFeedbackTypeWarning;
        else if (type == "notificationError") feedbackType = UINotificationFeedbackTypeError;
        
        [notificationGenerator notificationOccurred:feedbackType];
        [notificationGenerator prepare];
        
    } else if (type == "selection") {
        if (selectionGenerator == nil) {
            selectionGenerator = [[UISelectionFeedbackGenerator alloc] init];
            [selectionGenerator prepare];
        }
        
        [selectionGenerator selectionChanged];
        [selectionGenerator prepare];
    }
}

facebook::jsi::Value TurboHapticsHostObject::get(facebook::jsi::Runtime& runtime, const facebook::jsi::PropNameID& name) {
    auto propertyName = name.utf8(runtime);
    
    if (propertyName == "trigger") {
        return facebook::jsi::Function::createFromHostFunction(runtime,
            name,
            1,
            [this](facebook::jsi::Runtime& runtime,
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
