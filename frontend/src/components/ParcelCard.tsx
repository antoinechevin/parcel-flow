import React from 'react';
import { Card, Text, Badge } from 'react-native-paper';
import { StyleSheet, View } from 'react-native';

export type Parcel = {
  id: string;
  trackingNumber: string;
  deadline: string;
  status: 'AVAILABLE' | 'PICKED_UP';
};

interface ParcelCardProps {
  parcel: Parcel;
}

export const ParcelCard: React.FC<ParcelCardProps> = ({ parcel }) => {
  return (
    <Card style={styles.card}>
      <Card.Content>
        <View style={styles.header}>
          <Text variant="titleLarge">{parcel.trackingNumber}</Text>
          <Badge size={24}>{parcel.status}</Badge>
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
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
});
