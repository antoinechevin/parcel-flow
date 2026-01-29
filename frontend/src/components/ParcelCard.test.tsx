import React from 'react';
import { render } from '@testing-library/react-native';
import { ParcelCard } from './ParcelCard';
import { Parcel } from '../types';

describe('ParcelCard', () => {
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
    }
  };

  it('renders parcel details correctly', () => {
    const { getByText } = render(<ParcelCard parcel={mockParcel} />);
    
    expect(getByText('SHOES-123')).toBeTruthy();
    expect(getByText('Deadline: 2026-01-23')).toBeTruthy();
    expect(getByText('AVAILABLE')).toBeTruthy();
  });

  it('renders expired parcel with EXPIRED status', () => {
    const expiredParcel: any = {
      ...mockParcel,
      status: 'EXPIRED'
    };
    const { getByText } = render(<ParcelCard parcel={expiredParcel} />);
    
    expect(getByText('EXPIRED')).toBeTruthy();
  });
});
