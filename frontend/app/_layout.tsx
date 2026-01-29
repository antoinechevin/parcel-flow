import React, { useEffect, useState } from 'react';
import { Stack, useRouter, useSegments, useRootNavigationState } from 'expo-router';
import { PaperProvider, MD3LightTheme } from 'react-native-paper';
import { useAuthStore } from '../src/core/auth/authStore';

const theme = {
  ...MD3LightTheme,
  colors: {
    ...MD3LightTheme.colors,
    warning: '#FFB300', // Orange for MEDIUM
    info: '#2196F3',    // Blue for LOW
  },
};

export default function RootLayout() {
  const apiKey = useAuthStore((state) => state.apiKey);
  const segments = useSegments();
  const router = useRouter();
  const navigationState = useRootNavigationState();
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  useEffect(() => {
    // Ne rien faire tant que l'app n'est pas montée ou que la navigation n'est pas prête
    if (!isMounted || !navigationState?.key) return;

    const inAuthGroup = segments[0] === 'login';

    if (!apiKey && !inAuthGroup) {
      // Redirect to the login page if not authenticated
      router.replace('/login');
    } else if (apiKey && inAuthGroup) {
      // Redirect away from the login page if authenticated
      router.replace('/');
    }
  }, [apiKey, segments, isMounted, navigationState?.key]);

  if (!isMounted || !navigationState?.key) {
    return null; // Ou un écran de chargement très simple
  }

  return (
    <PaperProvider theme={theme}>
      <Stack screenOptions={{ headerShown: false }}>
        <Stack.Screen name="login" options={{ title: 'Connexion' }} />
        <Stack.Screen name="index" options={{ title: 'Mes Colis' }} />
      </Stack>
    </PaperProvider>
  );
}
