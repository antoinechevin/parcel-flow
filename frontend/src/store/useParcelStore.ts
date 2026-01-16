import { create } from 'zustand';
import { Parcel, fetchParcels, createParcel } from '../api/parcels';

interface ParcelState {
  parcels: Parcel[];
  isLoading: boolean;
  error: string | null;
  loadParcels: () => Promise<void>;
  addParcel: (id: string, label: string) => Promise<void>;
}

export const useParcelStore = create<ParcelState>((set) => ({
  parcels: [],
  isLoading: false,
  error: null,
  loadParcels: async () => {
    set({ isLoading: true, error: null });
    try {
      const parcels = await fetchParcels();
      set({ parcels, isLoading: false });
    } catch (error) {
      set({ error: 'Failed to load parcels', isLoading: false });
    }
  },
  addParcel: async (id: string, label: string) => {
    set({ isLoading: true, error: null });
    try {
      const newParcel = await createParcel(id, label);
      set((state) => ({
        parcels: [...state.parcels, newParcel],
        isLoading: false,
      }));
    } catch (error) {
      set({ error: 'Failed to create parcel', isLoading: false });
      throw error;
    }
  },
}));
