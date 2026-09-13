# 🌀 Turbo Haptics

Fast, **worklet-compatible** haptic feedback for React Native.

## Features

- 🏎️ Zero-delay haptic feedback, powered by JSI
- 🪄 Compatible with Reanimated and Gesture Handler worklets
- 🎯 9 different haptic patterns (impact, notification, selection)
- 📱 iOS and Android support
- 🪶 Lightweight, zero dependencies

## Installation

```sh
yarn add react-native-turbo-haptics
cd ios && pod install
```

## Compatibility

Paper is supported on React Native 0.72–0.81. The New Architecture is supported
on React Native 0.75 or newer.

## Threading

On iOS, UI-thread worklets invoke UIKit directly. Calls from the React Native
JavaScript thread or another worker thread are forwarded to the main queue, as
required by UIKit. Android invokes the vibrator from the calling runtime thread.
The API remains fire-and-forget on every path.

## Usage

```ts
import { triggerHaptics } from 'react-native-turbo-haptics';

// In any JavaScript context:
triggerHaptics('selection');

// In worklets:
Gesture.Tap()
  .onBegin(() => triggerHaptics('soft'));
```

```ts
// Available haptic types:
const HapticTypes = {
  impactHeavy: 'impactHeavy',
  impactLight: 'impactLight',
  impactMedium: 'impactMedium',
  notificationError: 'notificationError',
  notificationSuccess: 'notificationSuccess',
  notificationWarning: 'notificationWarning',
  rigid: 'rigid',
  selection: 'selection',
  soft: 'soft',
};
```

## License

MIT
