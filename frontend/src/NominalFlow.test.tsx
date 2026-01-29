import React from 'react';
import { render, waitFor, screen } from '@testing-library/react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { Provider as PaperProvider } from 'react-native-paper';
import ParcelListScreen from '../app/index';

// Mock useAuthStore
jest.mock('./core/auth/authStore', () => ({
  useAuthStore: (selector: any) => selector({ apiKey: 'test-key' }),
}));

// Mock data
const mockDashboardData = [
  {
    pickupPoint: {
      id: 'pp-1',
      name: 'Relais Colis - Epicerie du Coin',
      rawAddress: '123 Rue de la Paix, 75002 Paris',
      openingHours: '08:00 - 20:00'
    },
    parcels: [
      {
        id: 'parcel-1',
        trackingNumber: 'TRK123456',
        deadline: '2026-02-01',
        status: 'AVAILABLE'
      }
    ]
  }
];

// Mock fetch globalement
global.fetch = jest.fn() as jest.Mock;

describe('Nominal Flow: Parcel List Display', () => {
  beforeEach(() => {
    (global.fetch as jest.Mock).mockClear();
  });

  it('renders the list of parcels when API call is successful', async () => {
    (global.fetch as jest.Mock).mockImplementationOnce(() =>
      Promise.resolve({
        ok: true,
        status: 200,
        json: () => Promise.resolve(mockDashboardData),
      })
    );

    render(
      <SafeAreaProvider initialMetrics={{ frame: { x: 0, y: 0, width: 0, height: 0 }, insets: { top: 0, left: 0, right: 0, bottom: 0 } }}>
        <PaperProvider>
          <ParcelListScreen />
        </PaperProvider>
      </SafeAreaProvider>
    );

    // 1. Vérifie qu'on voit l'indicateur de chargement initialement (optionnel mais recommandé)
    // Note: Le chargement est souvent très rapide en test, on passe direct au waitFor

    // 2. Attend que les données soient affichées
    await waitFor(() => {
      expect(screen.getByText('Relais Colis - Epicerie du Coin')).toBeTruthy();
    });

    // 3. Vérifie que l'adresse est présente
    expect(screen.getByText('123 Rue de la Paix, 75002 Paris')).toBeTruthy();

    // 4. Vérifie qu'on a bien un colis listé (via le badge qui contient le nombre)
    // Le badge affiche juste le chiffre.
    expect(screen.getByText('1')).toBeTruthy();
  });

  it('displays an error message when API call fails', async () => {
    (global.fetch as jest.Mock).mockImplementationOnce(() =>
      Promise.resolve({
        ok: false,
        status: 500,
      })
    );

    render(
      <SafeAreaProvider initialMetrics={{ frame: { x: 0, y: 0, width: 0, height: 0 }, insets: { top: 0, left: 0, right: 0, bottom: 0 } }}>
        <PaperProvider>
          <ParcelListScreen />
        </PaperProvider>
      </SafeAreaProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('Error')).toBeTruthy();
      expect(screen.getByText('Failed to fetch dashboard data')).toBeTruthy();
    });
  });
});
