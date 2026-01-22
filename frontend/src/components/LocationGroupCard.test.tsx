import React from 'react';
import { render } from '@testing-library/react-native';
import { LocationGroupCard } from './LocationGroupCard';
import { LocationGroup } from '../types';

describe('LocationGroupCard', () => {
  const mockGroup: LocationGroup = {
    pickupPoint: {
      id: 'pp-1',
      name: 'Relais Colis',
      rawAddress: '12 rue de la Paix',
      openingHours: '08:00-19:00',
    },
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

  it('renders pickup point details and parcels', () => {
    const { getByText } = render(<LocationGroupCard group={mockGroup} />);
    
    expect(getByText('Relais Colis')).toBeTruthy();
    expect(getByText('12 rue de la Paix')).toBeTruthy();
    expect(getByText('🕒 08:00-19:00')).toBeTruthy();
    expect(getByText('SHOES-123')).toBeTruthy();
  });
});