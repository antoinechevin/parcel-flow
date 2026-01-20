import React from 'react';
import { View, StyleSheet } from 'react-native';
import { Text, Button, Appbar } from 'react-native-paper';

export default function Heartbeat() {
  return (
    <View style={styles.container}>
      <Appbar.Header>
        <Appbar.Content title="Parcel-Flow Heartbeat" />
      </Appbar.Header>
      <View style={styles.content}>
        <Text variant="headlineMedium">Walking Skeleton</Text>
        <Text variant="bodyLarge" style={styles.text}>
          Frontend is correctly initialized with Expo Router and RN Paper.
        </Text>
        <Button mode="contained" onPress={() => console.log('Heartbeat pressed')}>
          Heartbeat
        </Button>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  text: {
    marginVertical: 20,
    textAlign: 'center',
  },
});
