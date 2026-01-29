import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { TextInput, Button, Text, Surface } from 'react-native-paper';
import { useRouter } from 'expo-router';
import { useAuthStore } from '../src/core/auth/authStore';

export default function LoginScreen() {
  const [password, setPassword] = useState('');
  const setApiKey = useAuthStore((state) => state.setApiKey);
  const router = useRouter();

  const handleLogin = () => {
    if (password.trim()) {
      setApiKey(password);
      router.replace('/');
    }
  };

  return (
    <View style={styles.container}>
      <Surface style={styles.surface} elevation={2}>
        <Text variant="headlineMedium" style={styles.title}>Parcel-Flow</Text>
        <Text variant="bodyMedium" style={styles.subtitle}>Sécurisez votre accès</Text>
        <TextInput
          label="Mot de passe"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
          mode="outlined"
          style={styles.input}
        />
        <Button mode="contained" onPress={handleLogin} style={styles.button}>
          Connexion
        </Button>
      </Surface>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 20,
    backgroundColor: '#f5f5f5',
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
    marginBottom: 16,
  },
  button: {
    width: '100%',
    marginTop: 8,
  },
});
