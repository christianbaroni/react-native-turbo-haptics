import { NativeModules } from 'react-native';
import TurboHapticsModule from './NativeTurboHaptics';

declare global {
  var __turboModuleProxy: unknown | undefined;
  function nativeCallSyncHook(): unknown;
  function createHapticFeedback(): {
    trigger: (type: import('./index').HapticType) => void;
  };
}

const noop = () => {
  'worklet';
  return;
};

export const initTurboHaptics = () => {
  // Check if already initialized
  if (global.createHapticFeedback == null) {
    try {
      let success = false;
      const module = NativeModules.TurboHaptics;

      // [Pre-0.74] Check for JSI environment and install bindings
      if (global.nativeCallSyncHook != null && module?.install) {
        success = module.install();
      }
      // [RN 0.74+] Check for JSI environment and install bindings
      else if (global.__turboModuleProxy != null) {
        try {
          success = TurboHapticsModule.install();
        } catch (e) {
          console.warn('TurboHaptics: TurboModule not available:', e);
        }
      }

      if (!success) {
        console.warn('TurboHaptics: Failed to initialize');
        return null;
      }

      if (global.createHapticFeedback == null) {
        console.warn('TurboHaptics: Failed to create haptic feedback');
        return null;
      }
    } catch (e) {
      console.warn('TurboHaptics: Initialization failed:', e);
      return null;
    }
  }

  return global.createHapticFeedback ? global.createHapticFeedback() : null;
};

// Initialize module
const haptics = initTurboHaptics() || {
  trigger: noop,
};

export const internalTrigger = haptics.trigger;
