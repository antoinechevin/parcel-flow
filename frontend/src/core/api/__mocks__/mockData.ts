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
  }
];
