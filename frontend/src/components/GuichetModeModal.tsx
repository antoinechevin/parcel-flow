import React from 'react';
import { Modal, Portal, Text, Button, IconButton, useTheme } from 'react-native-paper';
import { StyleSheet, View, Image, Dimensions } from 'react-native';
import QRCode from 'react-native-qrcode-svg';
import { Parcel } from '../types';

interface GuichetModeModalProps {
  visible: boolean;
  onDismiss: () => void;
  parcel: Parcel;
}

export const GuichetModeModal: React.FC<GuichetModeModalProps> = ({ visible, onDismiss, parcel }) => {
  const theme = useTheme();
  
  // Use pickupCode for QR if present, otherwise trackingNumber
  const qrValue = parcel.pickupCode || parcel.trackingNumber;
  const [imgError, setImgError] = React.useState(false);

  return (
    <Portal>
      <Modal 
        visible={visible} 
        onDismiss={onDismiss} 
        contentContainerStyle={styles.container}
      >
        <View style={styles.content}>
          <IconButton
            icon="close"
            size={30}
            onPress={onDismiss}
            style={styles.closeButton}
          />
          
          <Text variant="headlineSmall" style={styles.carrier}>
            {parcel.pickupPoint.name}
          </Text>
          <Text variant="bodyMedium" style={styles.carrierSub}>
            {parcel.trackingNumber}
          </Text>

          <View style={styles.qrSection}>
            {parcel.qrCodeUrl && !imgError ? (
              <Image 
                source={{ uri: parcel.qrCodeUrl }} 
                style={styles.qrImage}
                resizeMode="contain"
                onError={() => setImgError(true)}
              />
            ) : (
              <View>
                <QRCode
                  value={qrValue}
                  size={250}
                  backgroundColor="white"
                  color="black"
                />
                {imgError && (
                  <Text style={styles.fallbackLabel}>Image unavailable, using local QR</Text>
                )}
              </View>
            )}
          </View>

          {parcel.pickupCode && (
            <View style={styles.codeSection}>
              <Text style={styles.codeLabel}>CODE DE RETRAIT</Text>
              <Text style={styles.hugeCode}>{parcel.pickupCode}</Text>
            </View>
          )}

          <Button 
            mode="contained" 
            onPress={onDismiss} 
            style={styles.doneButton}
            labelStyle={styles.doneButtonLabel}
          >
            TERMINÉ
          </Button>
        </View>
      </Modal>
    </Portal>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'white',
    padding: 20,
    margin: 10,
    borderRadius: 16,
    // Ensure high contrast
    borderWidth: 2,
    borderColor: 'black',
  },
  content: {
    alignItems: 'center',
    position: 'relative',
    paddingTop: 30,
  },
  closeButton: {
    position: 'absolute',
    right: -10,
    top: -10,
  },
  carrier: {
    fontWeight: 'bold',
    textAlign: 'center',
    color: 'black',
  },
  carrierSub: {
    color: '#666',
    marginBottom: 20,
  },
  qrSection: {
    padding: 20,
    backgroundColor: 'white',
    borderRadius: 10,
    marginBottom: 20,
    // Add shadow/border for the QR code to stand out
    borderWidth: 1,
    borderColor: '#eee',
  },
  qrImage: {
    width: 250,
    height: 250,
  },
  fallbackLabel: {
    fontSize: 10,
    color: '#999',
    textAlign: 'center',
    marginTop: 8,
  },
  codeSection: {
    alignItems: 'center',
    marginBottom: 30,
  },
  codeLabel: {
    fontSize: 16,
    fontWeight: 'bold',
    color: 'black',
    letterSpacing: 2,
  },
  hugeCode: {
    fontSize: 72,
    fontWeight: '900',
    color: 'black',
    letterSpacing: 4,
  },
  doneButton: {
    width: '100%',
    paddingVertical: 8,
    backgroundColor: 'black',
  },
  doneButtonLabel: {
    fontSize: 18,
    fontWeight: 'bold',
    color: 'white',
  },
});
