# 🌀 Turbo Haptics

Fast, **worklet-compatible** haptic feedback for React Native.

## Features

- 🏎️ JSI-powered haptic feedback
- 🪄 Compatible with Reanimated and Gesture Handler worklets
- 🎯 9 haptic types (impact, notification, selection)
- 📱 iOS and Android support
- 🪶 Lightweight, zero dependencies

## Installation

```sh
yarn add react-native-turbo-haptics
cd ios && pod install
```

## Compatibility

Paper is supported on React Native 0.72–0.81. The New Architecture is supported
on React Native 0.75 or newer. Android supports API 21+; the library compiles
against SDK 34 or the app’s compile SDK, whichever is newer.

Android haptics respect system settings and require an active app window.

## Threading

UI-thread worklets invoke the platform directly. Calls from the React Native
JavaScript thread or another worker thread are forwarded to the main queue.
The API is fire-and-forget on both platforms.

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
