import React from 'react';
import { render, fireEvent, waitFor, screen } from '@testing-library/react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import Heartbeat from './components/Heartbeat';

// Mock fetch globalement
global.fetch = jest.fn(() =>
  Promise.resolve({
    ok: true,
    status: 200,
    json: () => Promise.resolve({ status: 'UP', message: 'Parcel-Flow Backend is running' }),
  })
) as jest.Mock;

describe('Heartbeat Component', () => {
  it('calls the backend API and displays the message when button is pressed', async () => {
    const { getByText } = render(
      <SafeAreaProvider initialMetrics={{ frame: { x: 0, y: 0, width: 0, height: 0 }, insets: { top: 0, left: 0, right: 0, bottom: 0 } }}>
        <Heartbeat />
      </SafeAreaProvider>
    );
    
    // Le bouton existe
    const button = getByText('Heartbeat');
    expect(button).toBeTruthy();

    // On appuie sur le bouton
    fireEvent.press(button);

    // On vérifie que fetch a été appelé
    await waitFor(() => {
        expect(global.fetch).toHaveBeenCalledWith(expect.stringContaining('/heartbeat'));
    });

    // On vérifie que le message s'affiche
    await waitFor(() => {
        expect(getByText('Parcel-Flow Backend is running')).toBeTruthy();
    });
  });
});