//
//  TurboHapticsModule.h
//
//  Created by Christian Baroni on 11/10/24.
//  Copyright © 2024 Christian Baroni. All rights reserved.
//

#ifdef RCT_NEW_ARCH_ENABLED
#import <TurboHapticsSpec/TurboHapticsSpec.h>

@interface TurboHapticsModule : NSObject <NativeTurboHapticsSpec>
@end
#else
#import <React/RCTBridgeModule.h>

@interface TurboHapticsModule : NSObject <RCTBridgeModule>
@end
#endif
