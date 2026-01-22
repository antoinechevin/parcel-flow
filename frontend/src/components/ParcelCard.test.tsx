import React from 'react';
import { render } from '@testing-library/react-native';
import { ParcelCard, Parcel } from './ParcelCard';

describe('ParcelCard', () => {
  const mockParcel: Parcel = {
    id: '1',
    trackingNumber: 'SHOES-123',
    deadline: '2026-01-23',
    status: 'AVAILABLE',
  };

  it('renders parcel details correctly', () => {
    const { getByText } = render(<ParcelCard parcel={mockParcel} />);
    
    expect(getByText('SHOES-123')).toBeTruthy();
    expect(getByText('Deadline: 2026-01-23')).toBeTruthy();
    expect(getByText('AVAILABLE')).toBeTruthy();
  });
});
