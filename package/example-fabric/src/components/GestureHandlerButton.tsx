import React, { MutableRefObject, useMemo } from 'react';
import { StyleProp, ViewProps, ViewStyle } from 'react-native';
import {
  Gesture,
  GestureDetector,
  GestureStateChangeEvent,
  GestureType,
  LongPressGesture,
  LongPressGestureHandlerEventPayload,
  TapGesture,
  TapGestureHandlerEventPayload,
} from 'react-native-gesture-handler';
import Animated, {
  AnimatedStyle,
  Easing,
  runOnJS,
  useAnimatedStyle,
  useSharedValue,
  withTiming,
  WithTimingConfig,
} from 'react-native-reanimated';
import { HapticType, triggerHaptics } from 'react-native-turbo-haptics';

const LONG_PRESS_DURATION_IN_MS = 500;

const buttonPressConfig: WithTimingConfig = {
  duration: 160,
  easing: Easing.bezier(0.25, 0.46, 0.45, 0.94),
};

export type GestureHandlerButtonProps = {
  children: React.ReactNode;
  disableHaptics?: boolean;
  disableScale?: boolean;
  disabled?: boolean;
  hapticTrigger?: 'tap-end' | 'tap-start';
  hapticType?: HapticType;
  longPressDuration?: number;
  longPressRef?: MutableRefObject<LongPressGesture>;
  onLongPressEndWorklet?: (success?: boolean) => void;
  onLongPressJS?: (
    e?: GestureStateChangeEvent<LongPressGestureHandlerEventPayload>,
  ) => void;
  onLongPressWorklet?: (
    e?: GestureStateChangeEvent<LongPressGestureHandlerEventPayload>,
  ) => void;
  onPressJS?: (
    e?: GestureStateChangeEvent<TapGestureHandlerEventPayload>,
  ) => void;
  onPressStartWorklet?: (
    e?: GestureStateChangeEvent<TapGestureHandlerEventPayload>,
  ) => void;
  onPressWorklet?: (
    e?: GestureStateChangeEvent<TapGestureHandlerEventPayload>,
  ) => void;
  pointerEvents?: ViewProps['pointerEvents'];
  scaleTo?: number;
  simultaneousWithExternalGesture?: MutableRefObject<GestureType>;
  style?: StyleProp<ViewStyle> | AnimatedStyle;
  tapRef?: MutableRefObject<TapGesture>;
};

export function GestureHandlerButton({
  children,
  disableHaptics = false,
  disableScale = false,
  disabled = false,
  hapticTrigger = 'tap-start',
  hapticType = 'selection',
  longPressDuration = LONG_PRESS_DURATION_IN_MS,
  longPressRef,
  onLongPressEndWorklet,
  onLongPressJS,
  onLongPressWorklet,
  onPressJS,
  onPressStartWorklet,
  onPressWorklet,
  pointerEvents = 'box-only',
  scaleTo = 0.86,
  simultaneousWithExternalGesture,
  style,
  tapRef,
}: GestureHandlerButtonProps) {
  const isPressed = useSharedValue(false);

  const pressStyle = useAnimatedStyle(() => {
    if (disableScale) {
      return {};
    }
    return {
      transform: [
        { scale: withTiming(isPressed.value ? scaleTo : 1, buttonPressConfig) },
      ],
    };
  });

  const gesture = useMemo(() => {
    const tap = Gesture.Tap()
      .enabled(!disabled)
      .onBegin(e => {
        if (!disableScale) {
          isPressed.value = true;
        }
        if (!disableHaptics && hapticTrigger === 'tap-start') {
          triggerHaptics(hapticType);
        }
        onPressStartWorklet?.(e);
      })
      .onEnd(e => {
        if (!disableScale) {
          isPressed.value = false;
        }
        if (!disableHaptics && hapticTrigger === 'tap-end') {
          triggerHaptics(hapticType);
        }
        onPressWorklet?.(e);
        if (onPressJS) {
          runOnJS(onPressJS)(e);
        }
      })
      .onFinalize(() => {
        if (!disableScale) {
          isPressed.value = false;
        }
      });

    if (tapRef) {
      tap.withRef(tapRef);
    }
    if (simultaneousWithExternalGesture) {
      tap.simultaneousWithExternalGesture(simultaneousWithExternalGesture);
    }

    const longPressEnabled = !!(
      onLongPressEndWorklet ||
      onLongPressJS ||
      onLongPressWorklet
    );

    if (!longPressEnabled) {
      return tap;
    }

    const longPress = Gesture.LongPress()
      .enabled(!disabled)
      .minDuration(longPressDuration)
      .onStart(e => {
        if (!disableScale) {
          isPressed.value = true;
        }
        if (!disableHaptics) {
          triggerHaptics(hapticType);
        }
        onLongPressWorklet?.(e);
        if (onLongPressJS) {
          runOnJS(onLongPressJS)(e);
        }
      })
      .onFinalize((_, success) => {
        if (!disableScale) {
          isPressed.value = false;
        }
        onLongPressEndWorklet?.(success);
      });

    if (longPressRef) {
      longPress.withRef(longPressRef);
    }

    const tapEnabled = !!(onPressStartWorklet || onPressJS || onPressWorklet);

    if (!tapEnabled) {
      return longPress;
    }

    return Gesture.Race(tap, longPress);
  }, [
    disableHaptics,
    disableScale,
    disabled,
    hapticTrigger,
    hapticType,
    isPressed,
    longPressDuration,
    longPressRef,
    onLongPressEndWorklet,
    onLongPressJS,
    onLongPressWorklet,
    onPressJS,
    onPressStartWorklet,
    onPressWorklet,
    simultaneousWithExternalGesture,
    tapRef,
  ]);

  return (
    <GestureDetector gesture={gesture}>
      <Animated.View
        accessible
        accessibilityRole="button"
        pointerEvents={pointerEvents}
        style={[style, pressStyle]}>
        {children}
      </Animated.View>
    </GestureDetector>
  );
}
