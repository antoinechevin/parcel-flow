import React, { useEffect, useState } from 'react';
import { View, FlatList, StyleSheet } from 'react-native';
import { Appbar, Card, Title, Paragraph, FAB, Portal, Modal, TextInput, Button, Text } from 'react-native-paper';
import { useParcelStore } from '../store/useParcelStore';

export const DashboardScreen = () => {
  const { parcels, loadParcels, addParcel, isLoading, error } = useParcelStore();
  const [visible, setVisible] = useState(false);
  const [id, setId] = useState('');
  const [label, setLabel] = useState('');

  useEffect(() => {
    loadParcels();
  }, [loadParcels]);

  const showModal = () => setVisible(true);
  const hideModal = () => setVisible(false);

  const handleAdd = async () => {
    if (id && label) {
      await addParcel(id, label);
      setId('');
      setLabel('');
      hideModal();
    }
  };

  return (
    <View style={styles.container}>
      <Appbar.Header>
        <Appbar.Content title="Parcel Flow" />
      </Appbar.Header>

      {error && <Text style={styles.error}>{error}</Text>}

      <FlatList
        data={parcels}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.list}
        onRefresh={loadParcels}
        refreshing={isLoading}
        renderItem={({ item }) => (
          <Card style={styles.card}>
            <Card.Content>
              <Title>{item.label}</Title>
              <Paragraph>ID: {item.id}</Paragraph>
              <Paragraph>Status: {item.status}</Paragraph>
            </Card.Content>
          </Card>
        )}
      />

      <Portal>
        <Modal visible={visible} onDismiss={hideModal} contentContainerStyle={styles.modal}>
          <Title>New Parcel</Title>
          <TextInput
            label="Tracking ID"
            value={id}
            onChangeText={setId}
            style={styles.input}
          />
          <TextInput
            label="Label"
            value={label}
            onChangeText={setLabel}
            style={styles.input}
          />
          <Button mode="contained" onPress={handleAdd} loading={isLoading} style={styles.button}>
            Add Parcel
          </Button>
        </Modal>
      </Portal>

      <FAB
        style={styles.fab}
        icon="plus"
        onPress={showModal}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  list: {
    padding: 16,
  },
  card: {
    marginBottom: 16,
  },
  fab: {
    position: 'absolute',
    margin: 16,
    right: 0,
    bottom: 0,
  },
  modal: {
    backgroundColor: 'white',
    padding: 20,
    margin: 20,
    borderRadius: 8,
  },
  input: {
    marginBottom: 12,
    backgroundColor: 'white',
  },
  button: {
    marginTop: 8,
  },
  error: {
    color: 'red',
    textAlign: 'center',
    padding: 8,
  },
});
