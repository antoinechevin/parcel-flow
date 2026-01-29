import React from 'react';
import { Card, Text, Surface, Badge, useTheme } from 'react-native-paper';
import { StyleSheet, View } from 'react-native';
import { LocationGroup, Parcel, UrgencyLevel } from '../types';
import { ParcelCard } from './ParcelCard';

interface LocationGroupCardProps {
  group: LocationGroup;
}

const getUrgencyColor = (theme: any, urgency?: UrgencyLevel, isInactive?: boolean) => {
  if (isInactive) return theme.colors.outline || '#bdc3c7';
  switch (urgency) {
    case 'HIGH': return theme.colors.error;
    case 'MEDIUM': return theme.colors.warning || '#FDCB6E';
    case 'LOW': return theme.colors.info || '#2196F3';
    default: return '#bdc3c7';
  }
};

const getExpirationText = (daysUntil?: number, isInactive?: boolean) => {
  if (isInactive) return 'No active parcels';
  if (daysUntil === undefined || daysUntil === null) return null;

  if (daysUntil < 0) return 'Expired';
  if (daysUntil === 0) return 'Expires today';
  if (daysUntil === 1) return 'Expires in 1 day';
  return `Expires in ${daysUntil} days`;
};

export const LocationGroupCard: React.FC<LocationGroupCardProps> = ({ group }) => {
  const theme = useTheme();
  const isInactive = !group.parcels.some(p => p.status === 'AVAILABLE');
  const urgencyColor = getUrgencyColor(theme, group.urgency, isInactive);
  const expirationText = getExpirationText(group.daysUntilExpiration, isInactive);

  return (
    <Surface 
      style={[
        styles.container, 
        { borderLeftColor: urgencyColor, borderLeftWidth: 6 },
        isInactive && { opacity: 0.8 }
      ]} 
      elevation={2}
    >
      <View style={styles.content}>
        <View style={[styles.header, isInactive && { backgroundColor: theme.colors.surfaceVariant }]}>
          <View style={styles.titleRow}>
            <View>
              <Text 
                variant="titleMedium" 
                style={[styles.locationName, isInactive && { color: theme.colors.onSurfaceVariant }]}
              >
                {group.pickupPoint.name}
              </Text>
              {expirationText && (
                <Text 
                  variant="labelSmall" 
                  style={{ color: isInactive ? theme.colors.onSurfaceVariant : urgencyColor, fontWeight: 'bold' }}
                >
                  {expirationText}
                </Text>
              )}
            </View>
            <Badge size={20} style={[styles.badge, { backgroundColor: urgencyColor }]}>{group.parcels.length}</Badge>
          </View>
          <Text variant="bodySmall" style={[styles.address, isInactive && { color: theme.colors.onSurfaceVariant }]}>
            {group.pickupPoint.rawAddress}
          </Text>
          <Text variant="bodySmall" style={[styles.hours, isInactive && { color: theme.colors.outline }]}>
            🕒 {group.pickupPoint.openingHours}
          </Text>
        </View>
        <View style={styles.parcelList}>
          {group.parcels.map((parcel) => (
            <ParcelCard key={typeof parcel.id === 'string' ? parcel.id : parcel.id.value} parcel={parcel} />
          ))}
        </View>
      </View>
    </Surface>
  );
};

const styles = StyleSheet.create({
  container: {
    marginVertical: 12,
    marginHorizontal: 16,
    borderRadius: 12,
    backgroundColor: '#ffffff',
  },
  content: {
    borderRadius: 12,
    overflow: 'hidden',
  },
  header: {
    padding: 16,
    backgroundColor: '#f8f9fa',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  titleRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 4,
  },
  locationName: {
    fontWeight: 'bold',
    color: '#2d3436',
  },
  address: {
    color: '#636e72',
    marginBottom: 2,
  },
  hours: {
    color: '#0984e3',
    fontWeight: '500',
  },
  badge: {
    fontWeight: 'bold',
    color: '#fff',
  },
  parcelList: {
    paddingBottom: 8,
  },
});