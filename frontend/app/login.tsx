import React, { useState } from 'react';
import { View, StyleSheet, KeyboardAvoidingView, Platform, ScrollView } from 'react-native';
import { TextInput, Button, Text, Surface, HelperText, Divider } from 'react-native-paper';
import { useRouter } from 'expo-router';
import * as Haptics from 'expo-haptics';
import { useAuthStore, HEADER_NAME } from '../src/core/auth/authStore';
import { API_URL } from '../src/core/api/config';
import { DemoBanner } from '../src/components/DemoBanner';

export default function LoginScreen() {
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const setApiKey = useAuthStore((state) => state.setApiKey);
  const isDemoMode = useAuthStore((state) => state.isDemoMode);
  const setDemoMode = useAuthStore((state) => state.setDemoMode);
  
  const router = useRouter();

  const handleLogin = async () => {
    if (!password.trim()) return;

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${API_URL}/api/auth/verify`, {
        headers: {
          [HEADER_NAME]: password,
        },
      });

      if (response.ok) {
        setApiKey(password);
        router.replace('/');
      } else {
        setError('Mot de passe incorrect. Veuillez réessayer.');
      }
    } catch (err) {
      setError('Impossible de contacter le serveur.');
    } finally {
      setLoading(false);
    }
  };

  const enterDemoMode = () => {
    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);
    setDemoMode(true);
  };

  return (
    <KeyboardAvoidingView 
      style={styles.container} 
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      {isDemoMode && <DemoBanner />}

      <ScrollView contentContainerStyle={styles.scrollContent} keyboardShouldPersistTaps="handled">
        <View style={styles.loginContent}>
          <Surface style={styles.surface} elevation={2}>
            <Text variant="headlineMedium" style={styles.title}>Parcel-Flow</Text>
            <Text variant="bodyMedium" style={styles.subtitle}>Sécurisez votre accès</Text>
            <TextInput
              label="Mot de passe"
              value={password}
              onChangeText={(text) => {
                setPassword(text);
                setError(null);
              }}
              secureTextEntry
              mode="outlined"
              style={styles.input}
              error={!!error}
              disabled={loading}
            />
            {error && (
              <HelperText type="error" visible={!!error} style={styles.helperText}>
                {error}
              </HelperText>
            )}
            <Button 
              mode="contained" 
              onPress={handleLogin} 
              style={styles.button}
              loading={loading}
              disabled={loading || !password.trim()}
            >
              Connexion
            </Button>

            <Divider style={styles.divider} />

            <Button 
              mode="outlined" 
              onPress={enterDemoMode} 
              style={styles.button}
              icon="play-circle-outline"
              accessibilityLabel="Accéder à l'application en mode démonstration"
            >
              Essayer la démo
            </Button>
          </Surface>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
  },
  loginContent: {
    padding: 20,
  },
  surface: {
    padding: 30,
    borderRadius: 12,
    alignItems: 'center',
  },
  title: {
    marginBottom: 8,
    fontWeight: 'bold',
  },
  subtitle: {
    marginBottom: 24,
    color: '#666',
  },
  input: {
    width: '100%',
  },
  helperText: {
    width: '100%',
    textAlign: 'left',
    marginBottom: 8,
  },
  button: {
    width: '100%',
    marginTop: 8,
  },
  divider: {
    width: '100%',
    marginVertical: 16,
  },
});
