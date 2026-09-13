import { NativeModules } from 'react-native';
import TurboHapticsModule from './NativeTurboHaptics';

declare global {
  function createHapticFeedback(): {
    trigger: (type: import('./index').HapticType) => void;
  };
}

const noop = () => {
  'worklet';
};

export const initTurboHaptics = () => {
  if (globalThis.createHapticFeedback == null) {
    try {
      let success = false;
      const nativeModule = NativeModules.TurboHaptics;

      if (
        TurboHapticsModule &&
        typeof TurboHapticsModule.install === 'function'
      ) {
        success = TurboHapticsModule.install();
      } else if (nativeModule && typeof nativeModule.install === 'function') {
        success = nativeModule.install();
      }

      if (!success) {
        console.warn('TurboHaptics: Failed to initialize');
        return null;
      }

      if (globalThis.createHapticFeedback == null) {
        console.warn('TurboHaptics: Failed to create haptic feedback');
        return null;
      }
    } catch (e) {
      console.warn('TurboHaptics: Initialization failed:', e);
      return null;
    }
  }

  return globalThis.createHapticFeedback
    ? globalThis.createHapticFeedback()
    : null;
};

const haptics = initTurboHaptics() || {
  trigger: noop,
};

export const internalTrigger = haptics.trigger;
