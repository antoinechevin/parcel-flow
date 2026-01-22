import React from 'react';
import { Card, Text, Surface, Badge } from 'react-native-paper';
import { StyleSheet, View } from 'react-native';
import { LocationGroup, Parcel } from '../types';
import { ParcelCard } from './ParcelCard';

interface LocationGroupCardProps {
  group: LocationGroup;
}

const getGroupUrgencyColor = (parcels: Parcel[]) => {
  if (parcels.length === 0) return '#55EFC4';
  
  const activeParcels = parcels.filter(p => p.status === 'AVAILABLE');
  if (activeParcels.length === 0) return '#bdc3c7';

  const earliestDeadline = activeParcels.reduce((min, p) => {
    const d = new Date(p.deadline).getTime();
    return d < min ? d : min;
  }, new Date(activeParcels[0].deadline).getTime());

  const today = new Date().getTime();
  const diffDays = Math.ceil((earliestDeadline - today) / (1000 * 60 * 60 * 24));

  if (diffDays <= 1) return '#FF7675'; // Red
  if (diffDays <= 3) return '#FDCB6E'; // Orange
  return '#55EFC4'; // Green
};

export const LocationGroupCard: React.FC<LocationGroupCardProps> = ({ group }) => {
  const urgencyColor = getGroupUrgencyColor(group.parcels);

  return (
    <Surface style={[styles.container, { borderLeftColor: urgencyColor, borderLeftWidth: 6 }]} elevation={2}>
      <View style={styles.content}>
        <View style={styles.header}>
          <View style={styles.titleRow}>
            <Text variant="titleMedium" style={styles.locationName}>{group.pickupPoint.name}</Text>
            <Badge size={20} style={[styles.badge, { backgroundColor: urgencyColor }]}>{group.parcels.length}</Badge>
          </View>
          <Text variant="bodySmall" style={styles.address}>{group.pickupPoint.rawAddress}</Text>
          <Text variant="bodySmall" style={styles.hours}>🕒 {group.pickupPoint.openingHours}</Text>
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