import React from 'react';
import { Modal, Portal, Text, Button, IconButton, useTheme } from 'react-native-paper';
import { StyleSheet, View, Image } from 'react-native';
import QRCode from 'react-native-qrcode-svg';
import { useBrightnessControl } from '../hooks/useBrightnessControl';
import { Parcel } from '../types';

interface GuichetModeModalProps {
  visible: boolean;
  onDismiss: () => void;
  parcel: Parcel;
}

export const GuichetModeModal: React.FC<GuichetModeModalProps> = ({ visible, onDismiss, parcel }) => {
  const theme = useTheme();
  useBrightnessControl(visible);
  
  // Use pickupCode for QR if present, otherwise trackingNumber
  const qrValue = parcel.pickupCode || parcel.trackingNumber;
  const [imgError, setImgError] = React.useState(false);

  const renderBarcode = () => {
    switch (parcel.barcodeType) {
      case 'AZTEC':
      case 'QR_CODE':
        if (parcel.qrCodeUrl && !imgError) {
          return (
            <Image 
              source={{ uri: parcel.qrCodeUrl }} 
              style={styles.qrImage}
              resizeMode="contain"
              onError={() => setImgError(true)}
            />
          );
        }
        if (imgError) {
            return <Text style={styles.fallbackLabel}>Code non affichable, utiliser le PIN</Text>;
        }
        // Fallback for QR_CODE if no URL
        if(parcel.barcodeType === 'QR_CODE') {
            return (
                <View>
                    <QRCode
                    value={qrValue}
                    size={250}
                    backgroundColor="white"
                    color="black"
                    />
                    <Text style={styles.fallbackLabel}>Image unavailable, using local QR</Text>
                </View>
            );
        }
        return <Text style={styles.fallbackLabel}>Code non affichable, utiliser le PIN</Text>;
      case 'NONE':
      default:
        return null;
    }
  };

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

          <View style={[
            styles.qrSection, 
            parcel.barcodeType === 'NONE' && { minHeight: 0, padding: 0, borderWidth: 0 }
          ]}>
            {renderBarcode()}
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
    minHeight: 250,
    justifyContent: 'center',
    alignItems: 'center',
  },
  qrImage: {
    width: 250,
    height: 250,
  },
  fallbackLabel: {
    fontSize: 14,
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
