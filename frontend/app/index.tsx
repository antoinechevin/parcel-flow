import React from 'react';
import { FlatList, StyleSheet, View } from 'react-native';
import { Stack } from 'expo-router';
import { ActivityIndicator, Text } from 'react-native-paper';
import { useDashboard } from '../src/hooks/useDashboard';
import { LocationGroupCard } from '../src/components/LocationGroupCard';

export default function ParcelListScreen() {
  const { groups, loading, error } = useDashboard();

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
      {groups && groups.length > 0 ? (
        <FlatList
          data={groups}
          renderItem={({ item }) => <LocationGroupCard 
            group={item} 
            testID={`group-${item.pickupPoint.id}`}
          />}
          keyExtractor={(item) => item.pickupPoint.id}
          contentContainerStyle={styles.list}
        />
      ) : (
        <View style={styles.center}>
          <Text variant="bodyLarge">Aucun colis à récupérer pour le moment.</Text>
        </View>
      )}
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