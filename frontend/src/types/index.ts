export type PickupPoint = {
  id: string;
  name: string;
  rawAddress: string;
  openingHours: string;
};

export type BarcodeType = 'QR_CODE' | 'AZTEC' | 'CODE_128' | 'NONE';

export type Parcel = {
  id: { value: string } | string;
  trackingNumber: string;
  deadline: string;
  status: 'AVAILABLE' | 'PICKED_UP' | 'EXPIRED' | 'ARCHIVED';
  pickupPoint: PickupPoint;
  pickupCode?: string;
  qrCodeUrl?: string;
  barcodeType?: BarcodeType;
};

export type UrgencyLevel = 'HIGH' | 'MEDIUM' | 'LOW';

export type LocationGroup = {
  pickupPoint: PickupPoint;
  parcels: Parcel[];
  urgency?: UrgencyLevel;
  daysUntilExpiration?: number;
};
