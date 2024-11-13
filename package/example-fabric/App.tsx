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
          <GestureHandlerButton hapticType="soft" style={styles.button}>
            <Text style={styles.text}>Tap Me</Text>
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
    paddingHorizontal: 24,
  },
  root: {
    alignItems: 'center',
    backgroundColor: '#000000',
    flex: 1,
    justifyContent: 'center',
  },
  text: {
    color: '#000000',
    fontSize: 17,
    fontWeight: '700',
  },
});

export default App;
