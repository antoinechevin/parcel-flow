import React, { useEffect, useState } from 'react';
import { Stack, useRouter, useSegments, useRootNavigationState } from 'expo-router';
import { PaperProvider } from 'react-native-paper';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { useAuthStore } from '../src/core/auth/authStore';
import { AppTheme } from '../src/theme';

export default function RootLayout() {
  const apiKey = useAuthStore((state) => state.apiKey);
  const isDemoMode = useAuthStore((state) => state.isDemoMode);
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
    const isAuthenticated = !!apiKey || isDemoMode;

    if (!isAuthenticated && !inAuthGroup) {
      // Redirect to the login page if not authenticated
      router.replace('/login');
    } else if (isAuthenticated && inAuthGroup) {
      // Redirect away from the login page if authenticated
      router.replace('/');
    }
  }, [apiKey, isDemoMode, segments, isMounted, navigationState?.key]);

  if (!isMounted || !navigationState?.key) {
    return null; // Ou un écran de chargement très simple
  }

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <PaperProvider theme={AppTheme}>
        <Stack>
          <Stack.Screen name="login" options={{ headerShown: false }} />
          <Stack.Screen name="index" options={{ title: 'Mes Colis', headerShown: true }} />
        </Stack>
      </PaperProvider>
    </GestureHandlerRootView>
  );
}
