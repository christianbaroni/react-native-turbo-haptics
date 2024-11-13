import { internalTrigger } from './initModule';

export const HapticTypes = {
  impactHeavy: 'impactHeavy',
  impactLight: 'impactLight',
  impactMedium: 'impactMedium',
  notificationError: 'notificationError',
  notificationSuccess: 'notificationSuccess',
  notificationWarning: 'notificationWarning',
  rigid: 'rigid',
  selection: 'selection',
  soft: 'soft',
} as const;

export type HapticType = (typeof HapticTypes)[keyof typeof HapticTypes];

/**
 * #### `🌀 triggerHaptics 🌀`
 *
 * Triggers immediate haptic feedback.
 *
 * Compatible with worklets (Reanimated, Gesture Handler).
 *
 * @example
 * ```ts
 * // In a regular JavaScript context:
 * triggerHaptics('selection');
 *
 * // In a Reanimated worklet:
 * useAnimatedReaction(
 *   () => counter.value,
 *   (curr, prev) => {
 *     if (prev === null) return;
 *     if (curr < prev) triggerHaptics('soft');
 *     if (curr > prev) triggerHaptics('impactHeavy');
 *   },
 *   []
 * );
 * ```
 *
 * @param type - The type of haptic feedback to trigger
 */
export const triggerHaptics = internalTrigger;
