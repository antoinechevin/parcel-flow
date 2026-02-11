import React from 'react';
import { FlatList, StyleSheet, View } from 'react-native';
import { Stack } from 'expo-router';
import { ActivityIndicator, Text, Snackbar, Button } from 'react-native-paper';
import { useDashboard } from '../src/hooks/useDashboard';
import { LocationGroupCard } from '../src/components/LocationGroupCard';
import { AppTheme } from '../src/theme';
import { useAuthStore } from '../src/core/auth/authStore';
import { DemoBanner } from '../src/components/DemoBanner';

export default function ParcelListScreen() {
  const { groups, loading, error, archiveParcel, undoArchive, hasPendingArchive, pendingTrackingNumber } = useDashboard();
  const isDemoMode = useAuthStore((state) => state.isDemoMode);
  const setDemoMode = useAuthStore((state) => state.setDemoMode);

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
      <Stack.Screen 
        options={{ 
            title: 'Mes Colis',
            headerRight: () => isDemoMode ? (
                <Button 
                  mode="text" 
                  compact 
                  onPress={() => setDemoMode(false)}
                  labelStyle={{ fontSize: 10 }}
                >
                  QUITTER DÉMO
                </Button>
            ) : null
        }} 
      />

      {isDemoMode && <DemoBanner absolute={false} />}

      {groups && groups.length > 0 ? (
        <FlatList
          data={groups}
          renderItem={({ item }) => <LocationGroupCard 
            group={item} 
            onArchive={archiveParcel}
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

      <Snackbar
        visible={hasPendingArchive}
        onDismiss={() => {}} // Controlled by useDashboard timeout
        action={{
          label: 'ANNULER',
          onPress: undoArchive,
          textColor: AppTheme.colors.snackbarAction,
        }}
        duration={5000}
        theme={{
          colors: {
            onSurface: AppTheme.colors.snackbarBackground,
            inverseOnSurface: AppTheme.colors.snackbarText,
          }
        }}
      >
        <Text style={{ color: AppTheme.colors.snackbarText }}>
          Colis {pendingTrackingNumber} archivé.
        </Text>
      </Snackbar>
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
  },
  headerRight: {
    flexDirection: 'row',
    alignItems: 'center',
    marginRight: 16,
  }
});