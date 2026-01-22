import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { Text, Button, Appbar } from 'react-native-paper';

export default function Heartbeat() {
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleHeartbeat = async () => {
    setIsLoading(true);
    setStatusMessage(null);
    
    // Use environment variable or fallback to localhost
    // In Codespaces, set EXPO_PUBLIC_API_URL in .env.local
    const apiUrl = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';
    const fullUrl = `${apiUrl}/heartbeat`;

    console.log(`Starting heartbeat check to: ${fullUrl}`);
    try {
      const response = await fetch(fullUrl);
      console.log('Response received', response.status);
      if (!response.ok) {
        throw new Error(`HTTP status ${response.status}`);
      }
      const data = await response.json();
      setStatusMessage(data.message);
    } catch (error) {
      console.error('Heartbeat failed', error);
      setStatusMessage('Error connecting to backend: ' + (error instanceof Error ? error.message : String(error)));
    } finally {
      setIsLoading(false);
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
        
        {isLoading && (
           <Text variant="bodyMedium" style={{ ...styles.statusText, color: 'blue' }}>
             Connecting to backend...
           </Text>
        )}

        {statusMessage && !isLoading && (
          <Text variant="bodyMedium" style={styles.statusText}>
            {statusMessage}
          </Text>
        )}

        <Button mode="contained" onPress={handleHeartbeat} loading={isLoading} disabled={isLoading}>
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
