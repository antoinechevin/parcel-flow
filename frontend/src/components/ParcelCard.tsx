import React from 'react';
import { Card, Text, Badge } from 'react-native-paper';
import { StyleSheet, View } from 'react-native';

export type Parcel = {
  id: { value: string } | string;
  trackingNumber: string;
  deadline: string;
  status: 'AVAILABLE' | 'PICKED_UP';
};

interface ParcelCardProps {
  parcel: Parcel;
}

const getUrgencyColor = (deadline: string, status: string) => {
  if (status === 'PICKED_UP') return '#bdc3c7'; // Grey
  
  const today = new Date();
  const deadlineDate = new Date(deadline);
  const diffTime = deadlineDate.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays <= 1) return '#FF7675'; // Red (Urgent)
  if (diffDays <= 3) return '#FDCB6E'; // Orange (Soon)
  return '#55EFC4'; // Green (Safe)
};

export const ParcelCard: React.FC<ParcelCardProps> = ({ parcel }) => {
  const urgencyColor = getUrgencyColor(parcel.deadline, parcel.status);

  return (
    <Card style={[styles.card, { borderLeftColor: urgencyColor, borderLeftWidth: 5 }]}>
      <Card.Content>
        <View style={styles.header}>
          <Text variant="titleLarge">{parcel.trackingNumber}</Text>
          <Badge size={24} style={{ backgroundColor: urgencyColor }}>{parcel.status}</Badge>
        </View>
        <Text variant="bodyMedium">Deadline: {parcel.deadline}</Text>
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