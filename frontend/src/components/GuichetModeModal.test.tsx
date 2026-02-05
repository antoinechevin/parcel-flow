import React from 'react';
import { render, waitFor } from '@testing-library/react-native';
import { Provider as PaperProvider } from 'react-native-paper';
import { GuichetModeModal } from './GuichetModeModal';
import * as Brightness from 'expo-brightness';
import { Parcel } from '../types';

// Mock expo-brightness
jest.mock('expo-brightness', () => ({
  setBrightnessAsync: jest.fn(() => Promise.resolve()),
  getBrightnessAsync: jest.fn(() => Promise.resolve(0.5)),
  requestPermissionsAsync: jest.fn(() => Promise.resolve({ status: 'granted' })),
}));

describe('GuichetModeModal', () => {
  const mockParcel: Parcel = {
    id: '1',
    trackingNumber: 'SHOES-123',
    deadline: '2026-01-23',
    status: 'AVAILABLE',
    pickupPoint: {
      id: 'p1',
      name: 'Test Point',
      rawAddress: '123 Test St',
      openingHours: '9am-5pm'
    },
    pickupCode: '123456',
    barcodeType: 'QR_CODE'
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('sets brightness to 100% when opened and restores it when closed', async () => {
    const { unmount, rerender } = render(
      <PaperProvider>
        <GuichetModeModal visible={true} onDismiss={() => {}} parcel={mockParcel} />
      </PaperProvider>
    );

    // Should request permissions and get current brightness
    await waitFor(() => {
      expect(Brightness.getBrightnessAsync).toHaveBeenCalled();
    });

    // Should set brightness to 1.0
    await waitFor(() => {
      expect(Brightness.setBrightnessAsync).toHaveBeenCalledWith(1);
    });

    // Unmount to simulate closing (or set visible to false)
    unmount();

    // Should restore brightness to 0.5
    await waitFor(() => {
      expect(Brightness.setBrightnessAsync).toHaveBeenCalledWith(0.5);
    });
  });

  it('does not set brightness if permission is denied', async () => {
    (Brightness.requestPermissionsAsync as jest.Mock).mockResolvedValueOnce({ status: 'denied' });
    
    render(
      <PaperProvider>
        <GuichetModeModal visible={true} onDismiss={() => {}} parcel={mockParcel} />
      </PaperProvider>
    );

    await waitFor(() => {
      expect(Brightness.requestPermissionsAsync).toHaveBeenCalled();
    });

    // Should NOT set brightness to 1.0
    expect(Brightness.setBrightnessAsync).not.toHaveBeenCalledWith(1);
  });
});
