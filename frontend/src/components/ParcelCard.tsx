import React from 'react';
import { Card, Text, Badge, useTheme } from 'react-native-paper';
import { StyleSheet, View } from 'react-native';
import { Parcel } from '../types';

interface ParcelCardProps {
  parcel: Parcel;
}

const getUrgencyColor = (theme: any, deadline: string, status: string) => {
  if (status === 'PICKED_UP' || status === 'EXPIRED') return theme.colors.outline || '#bdc3c7';
  
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

export const ParcelCard: React.FC<ParcelCardProps> = ({ parcel }) => {
  const theme = useTheme();
  const isExpired = parcel.status === 'EXPIRED';
  const urgencyColor = getUrgencyColor(theme, parcel.deadline, parcel.status);

  return (
    <Card style={[
      styles.card, 
      { borderLeftColor: urgencyColor, borderLeftWidth: 5 },
      isExpired && { backgroundColor: theme.colors.surfaceVariant, opacity: 0.6 }
    ]}>
      <Card.Content>
        <View style={styles.header}>
          <Text variant="titleLarge" style={isExpired && { color: theme.colors.onSurfaceVariant }}>
            {parcel.trackingNumber}
          </Text>
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
    </Card>
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
});