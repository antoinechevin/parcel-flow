import React from 'react';
import { FlatList, StyleSheet, View } from 'react-native';
import { Stack } from 'expo-router';
import { ActivityIndicator, Text } from 'react-native-paper';
import { useParcels } from '../src/hooks/useParcels';
import { ParcelCard } from '../src/components/ParcelCard';

export default function ParcelListScreen() {
  const { parcels, loading, error } = useParcels();

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator animating={true} size="large" />
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.center}>
        <Text variant="headlineMedium" style={styles.error}>Error</Text>
        <Text>{error}</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Stack.Screen options={{ title: 'Mes Colis' }} />
      <FlatList
        data={parcels}
        renderItem={({ item }) => <ParcelCard parcel={item} />}
        keyExtractor={(item) => typeof item.id === 'string' ? item.id : item.id.value}
        contentContainerStyle={styles.list}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  list: {
    paddingVertical: 8,
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  error: {
    color: 'red',
    marginBottom: 8,
  }
});