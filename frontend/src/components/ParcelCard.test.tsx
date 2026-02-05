import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import { Provider as PaperProvider } from 'react-native-paper';
import { ParcelCard } from './ParcelCard';
import { Parcel } from '../types';

// Mock GuichetModeModal to avoid Portal issues in unit tests
jest.mock('./GuichetModeModal', () => ({
  GuichetModeModal: () => null,
}));

// Mock Swipeable to be able to trigger its actions
jest.mock('react-native-gesture-handler', () => {
  const React = require('react');
  const { View } = require('react-native');
  class Swipeable extends React.Component {
    render() {
      return <View testID="swipeable">{this.props.children}</View>;
    }
  }
  return {
    Swipeable,
    GestureHandlerRootView: ({ children }: any) => <View>{children}</View>,
  };
});

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
    const { getByText } = render(
      <PaperProvider>
        <ParcelCard parcel={mockParcel} />
      </PaperProvider>
    );
    
    expect(getByText('SHOES-123')).toBeTruthy();
    expect(getByText('Deadline: 2026-01-23')).toBeTruthy();
    expect(getByText('AVAILABLE')).toBeTruthy();
  });

  it('renders a swipeable container', () => {
    const { getByTestId } = render(
      <PaperProvider>
        <ParcelCard parcel={mockParcel} />
      </PaperProvider>
    );
    
    expect(getByTestId('swipeable')).toBeTruthy();
  });

  it('renders ARCHIVER button for non-available parcels', () => {
    const nonAvailableParcel: Parcel = { ...mockParcel, status: 'PICKED_UP' };
    const { getByText } = render(
      <PaperProvider>
        <ParcelCard parcel={nonAvailableParcel} onArchive={() => {}} />
      </PaperProvider>
    );
    
    expect(getByText('ARCHIVER')).toBeTruthy();
  });
});
