import React from 'react';
import { render } from '@testing-library/react-native';
import { LocationGroupCard } from './LocationGroupCard';
import { LocationGroup } from '../types';
import { Provider as PaperProvider, DefaultTheme } from 'react-native-paper';

const theme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    error: '#B00020', // Red for HIGH
    warning: '#FFB300', // Orange for MEDIUM (custom)
    info: '#2196F3', // Blue for LOW (custom)
  },
};

describe('LocationGroupCard Urgency', () => {
  const mockGroup: LocationGroup = {
    pickupPoint: {
      id: 'pp-1',
      name: 'Relais Colis',
      rawAddress: '12 rue de la Paix',
      openingHours: '08:00-19:00',
    },
    urgency: 'HIGH',
    daysUntilExpiration: 1,
    parcels: [
      {
        id: '1',
        trackingNumber: 'SHOES-123',
        deadline: '2026-01-23',
        status: 'AVAILABLE',
        pickupPoint: { id: 'pp-1', name: 'Relais Colis', rawAddress: '12 rue de la Paix', openingHours: '08:00-19:00' }
      },
    ],
  };

  it('displays "Expires in X days" label', () => {
    // For deadline 2026-01-23 and today being 2026-01-22 (from system prompt)
    // It should say "Expires in 1 day" or similar
    const { getByText } = render(
      <PaperProvider theme={theme}>
        <LocationGroupCard group={mockGroup} />
      </PaperProvider>
    );
    
    expect(getByText(/Expires in/i)).toBeTruthy();
  });

  it('uses urgency level from props if available', () => {
     // This test will fail because the current component calculates urgency internally 
     // and doesn't use the 'urgency' field from props yet.
  });
});
