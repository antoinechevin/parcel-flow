export type PickupPoint = {
  id: string;
  name: string;
  rawAddress: string;
  openingHours: string;
};

export type Parcel = {
  id: { value: string } | string;
  trackingNumber: string;
  deadline: string;
  status: 'AVAILABLE' | 'PICKED_UP';
  pickupPoint: PickupPoint;
};

export type UrgencyLevel = 'HIGH' | 'MEDIUM' | 'LOW';

export type LocationGroup = {
  pickupPoint: PickupPoint;
  parcels: Parcel[];
  urgency?: UrgencyLevel;
  daysUntilExpiration?: number;
};
