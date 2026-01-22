import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { Text, Button, Appbar } from 'react-native-paper';

export default function Heartbeat() {
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  const handleHeartbeat = async () => {
    try {
      // Dans un vrai cas, l'URL viendrait d'une config
      const response = await fetch('http://localhost:8080/heartbeat');
      const data = await response.json();
      setStatusMessage(data.message);
    } catch (error) {
      console.error('Heartbeat failed', error);
      setStatusMessage('Error connecting to backend');
    }
  };

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
        
        {statusMessage && (
          <Text variant="bodyMedium" style={styles.statusText}>
            {statusMessage}
          </Text>
        )}

        <Button mode="contained" onPress={handleHeartbeat}>
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
  statusText: {
    marginBottom: 20,
    color: 'green',
    fontWeight: 'bold',
  },
});
