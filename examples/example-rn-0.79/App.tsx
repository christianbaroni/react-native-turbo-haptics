import React from 'react';
import { StatusBar, StyleSheet, Text, View } from 'react-native';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { GestureHandlerButton } from './src/components/GestureHandlerButton';

function App(): React.JSX.Element {
  return (
    <>
      <StatusBar barStyle="light-content" />
      <GestureHandlerRootView>
        <View style={styles.root}>
          <GestureHandlerButton hapticType="impactLight" style={styles.button}>
            <Text style={styles.text}>impactLight</Text>
          </GestureHandlerButton>
          <GestureHandlerButton hapticType="impactMedium" style={styles.button}>
            <Text style={styles.text}>impactMedium</Text>
          </GestureHandlerButton>
          <GestureHandlerButton hapticType="impactHeavy" style={styles.button}>
            <Text style={styles.text}>impactHeavy</Text>
          </GestureHandlerButton>
          <GestureHandlerButton
            hapticType="notificationError"
            style={styles.button}>
            <Text style={styles.text}>notificationError</Text>
          </GestureHandlerButton>
          <GestureHandlerButton
            hapticType="notificationWarning"
            style={styles.button}>
            <Text style={styles.text}>notificationWarning</Text>
          </GestureHandlerButton>
          <GestureHandlerButton
            hapticType="notificationSuccess"
            style={styles.button}>
            <Text style={styles.text}>notificationSuccess</Text>
          </GestureHandlerButton>
          <GestureHandlerButton hapticType="rigid" style={styles.button}>
            <Text style={styles.text}>rigid</Text>
          </GestureHandlerButton>
          <GestureHandlerButton hapticType="selection" style={styles.button}>
            <Text style={styles.text}>selection</Text>
          </GestureHandlerButton>
          <GestureHandlerButton hapticType="soft" style={styles.button}>
            <Text style={styles.text}>soft</Text>
          </GestureHandlerButton>
        </View>
      </GestureHandlerRootView>
    </>
  );
}

const styles = StyleSheet.create({
  button: {
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    borderCurve: 'continuous',
    borderRadius: 24,
    height: 48,
    justifyContent: 'center',
    paddingHorizontal: 20,
  },
  root: {
    alignItems: 'center',
    backgroundColor: '#000000',
    flex: 1,
    flexDirection: 'column',
    gap: 28,
    justifyContent: 'center',
  },
  text: {
    color: '#000000',
    fontSize: 17,
    fontWeight: '700',
  },
});

export default App;
