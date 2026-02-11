import React from 'react';
import { StyleSheet } from 'react-native';
import { Surface, Text } from 'react-native-paper';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { AppTheme } from '../theme';

interface DemoBannerProps {
  absolute?: boolean;
}

export const DemoBanner: React.FC<DemoBannerProps> = ({ absolute = true }) => {
  const insets = useSafeAreaInsets();
  
  return (
    <Surface 
      style={[
        styles.demoBanner, 
        absolute ? { position: 'absolute', top: insets.top, left: 0, right: 0, zIndex: 5 } : { paddingTop: 8 }
      ]} 
      elevation={1}
    >
      <Text variant="labelMedium" style={styles.demoBannerText}>MODE DÉMO ACTIF (Données simulées)</Text>
    </Surface>
  );
};

const styles = StyleSheet.create({
  demoBanner: {
    padding: 8,
    backgroundColor: AppTheme.colors.demoBannerBackground,
    alignItems: 'center',
  },
  demoBannerText: {
    color: AppTheme.colors.demoBannerText,
    fontWeight: 'bold',
  }
});
