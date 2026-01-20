import React from 'react';
import { View, StyleSheet } from 'react-native';
import { Stack } from 'expo-router';
import Heartbeat from '../src/components/Heartbeat';

export default function Home() {
  return (
    <View style={styles.container}>
      <Stack.Screen options={{ title: 'Parcel-Flow' }} />
      <Heartbeat />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
