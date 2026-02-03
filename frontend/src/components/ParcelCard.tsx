import React, { useState } from 'react';
import { Card, Text, Badge, useTheme, Button } from 'react-native-paper';
import { StyleSheet, View } from 'react-native';
import { Parcel } from '../types';
import { GuichetModeModal } from './GuichetModeModal';

interface ParcelCardProps {
  parcel: Parcel;
  onArchive?: (trackingNumber: string) => void;
}

const getUrgencyColor = (theme: any, deadline: string, status: string) => {
  if (status === 'PICKED_UP' || status === 'EXPIRED' || status === 'ARCHIVED') return theme.colors.outline || '#bdc3c7';

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const deadlineDate = new Date(deadline);
  deadlineDate.setHours(0, 0, 0, 0);

  const diffTime = deadlineDate.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays <= 1) return theme.colors.error;
  if (diffDays <= 3) return theme.colors.warning || '#FDCB6E';
  return theme.colors.info || '#2196F3';
};

export const ParcelCard: React.FC<ParcelCardProps> = ({ parcel, onArchive }) => {
  const theme = useTheme();
  const [guichetVisible, setGuichetVisible] = useState(false);

  const isExpired = parcel.status === 'EXPIRED';
  const isAvailable = parcel.status === 'AVAILABLE';
  const canArchive = parcel.status !== 'ARCHIVED';
  const urgencyColor = getUrgencyColor(theme, parcel.deadline, parcel.status);

  return (
    <>
      <Card style={[
        styles.card, 
        { borderLeftColor: urgencyColor, borderLeftWidth: 5 },
        isExpired && { backgroundColor: theme.colors.surfaceVariant, opacity: 0.6 }
      ]}>
        <Card.Content>
          <View style={styles.header}>
            <View>
              <Text variant="titleLarge" style={isExpired && { color: theme.colors.onSurfaceVariant }}>
                {parcel.trackingNumber}
              </Text>
              <Text variant="bodySmall" style={styles.carrierText}>
                {parcel.pickupPoint.name}
              </Text>
            </View>
            <Badge 
              size={24} 
              style={{ 
                backgroundColor: isExpired ? theme.colors.outline : urgencyColor,
                color: isExpired ? theme.colors.surfaceVariant : undefined 
              }}
            >
              {parcel.status}
            </Badge>
          </View>
          <Text variant="bodyMedium" style={isExpired && { color: theme.colors.onSurfaceVariant }}>
            Deadline: {parcel.deadline}
          </Text>
        </Card.Content>
        <Card.Actions>
          {isAvailable && (
            <Button 
              icon="qrcode-scan" 
              mode="contained-tonal" 
              onPress={() => setGuichetVisible(true)}
              style={styles.actionButton}
            >
              GUICHET
            </Button>
          )}
          {canArchive && onArchive && (
            <Button 
              icon="archive-outline" 
              mode={isAvailable ? "outlined" : "contained-tonal"} 
              onPress={() => onArchive(parcel.trackingNumber)}
              style={styles.actionButton}
            >
              ARCHIVER
            </Button>
          )}
        </Card.Actions>
      </Card>

      <GuichetModeModal 
        visible={guichetVisible}
        onDismiss={() => setGuichetVisible(false)}
        parcel={parcel}
      />
    </>
  );
};

const styles = StyleSheet.create({
  card: {
    marginVertical: 8,
    marginHorizontal: 16,
    backgroundColor: '#ffffff',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  carrierText: {
    color: '#666',
  },
  actionButton: {
    flex: 1,
  }
});