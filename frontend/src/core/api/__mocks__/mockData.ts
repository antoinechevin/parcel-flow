import { LocationGroup } from '../../../types';

export const MOCK_PARCELS: LocationGroup[] = [
  {
    pickupPoint: {
      id: 'mock-1',
      name: 'BOULANGERIE DU COIN',
      rawAddress: '12 RUE DE LA RÉPUBLIQUE, 75001 PARIS',
      openingHours: '07:00 - 20:00'
    },
    parcels: [
      {
        id: 'p1',
        trackingNumber: '8877665544',
        deadline: new Date(Date.now() + 86400000).toISOString(), // Tomorrow
        status: 'AVAILABLE',
        pickupPoint: {
            id: 'mock-1',
            name: 'BOULANGERIE DU COIN',
            rawAddress: '12 RUE DE LA RÉPUBLIQUE, 75001 PARIS',
            openingHours: '07:00 - 20:00'
        },
        pickupCode: '123456',
        barcodeType: 'QR_CODE'
      }
    ],
    urgency: 'HIGH',
    daysUntilExpiration: 1
  },
  {
    pickupPoint: {
      id: 'mock-3',
      name: 'TABAC DES SPORTS',
      rawAddress: '45 AVENUE FOCH, 69006 LYON',
      openingHours: '08:00 - 19:00'
    },
    parcels: [
      {
        id: 'p3',
        trackingNumber: 'CHRONO_XYZ_789',
        deadline: new Date(Date.now() + 172800000).toISOString(), // In 2 days
        status: 'AVAILABLE',
        pickupPoint: {
            id: 'mock-3',
            name: 'TABAC DES SPORTS',
            rawAddress: '45 AVENUE FOCH, 69006 LYON',
            openingHours: '08:00 - 19:00'
        },
        pickupCode: 'B-99',
        barcodeType: 'CODE_128'
      },
      {
        id: 'p4',
        trackingNumber: '9988776655',
        deadline: new Date(Date.now() + 432000000).toISOString(), // In 5 days
        status: 'AVAILABLE',
        pickupPoint: {
            id: 'mock-3',
            name: 'TABAC DES SPORTS',
            rawAddress: '45 AVENUE FOCH, 69006 LYON',
            openingHours: '08:00 - 19:00'
        },
        barcodeType: 'QR_CODE'
      }
    ],
    urgency: 'MEDIUM',
    daysUntilExpiration: 2
  },
  {
    pickupPoint: {
      id: 'mock-2',
      name: 'VINTED GO LOCKER',
      rawAddress: 'STATION F, 75013 PARIS',
      openingHours: '24/7'
    },
    parcels: [
      {
        id: 'p2',
        trackingNumber: 'VINTED123456',
        deadline: new Date(Date.now() - 86400000).toISOString(), // Yesterday
        status: 'EXPIRED',
        pickupPoint: {
            id: 'mock-2',
            name: 'VINTED GO LOCKER',
            rawAddress: 'STATION F, 75013 PARIS',
            openingHours: '24/7'
        },
        barcodeType: 'CODE_128'
      }
    ],
    urgency: 'LOW',
    daysUntilExpiration: -1
  },
  {
    pickupPoint: {
      id: 'mock-4',
      name: 'PHARMACIE CENTRALE',
      rawAddress: '1 PLACE BELLECOUR, 69002 LYON',
      openingHours: '09:00 - 21:00'
    },
    parcels: [
      {
        id: 'p5',
        trackingNumber: 'UPS_MOCK_111',
        deadline: new Date(Date.now() + 864000000).toISOString(), // In 10 days
        status: 'AVAILABLE',
        pickupPoint: {
            id: 'mock-4',
            name: 'PHARMACIE CENTRALE',
            rawAddress: '1 PLACE BELLECOUR, 69002 LYON',
            openingHours: '09:00 - 21:00'
        },
        barcodeType: 'QR_CODE'
      }
    ],
    urgency: 'LOW',
    daysUntilExpiration: 10
  }
];