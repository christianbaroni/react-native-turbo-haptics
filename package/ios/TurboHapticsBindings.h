//
//  TurboHapticsBindings.h
//
//  Created by Christian Baroni on 11/10/24.
//  Copyright © 2024 Christian Baroni. All rights reserved.
//

#pragma once

#include <jsi/jsi.h>

namespace turbohaptics {
void cleanup();
void installHapticFeedback(facebook::jsi::Runtime& runtime);
} // namespace turbohaptics
