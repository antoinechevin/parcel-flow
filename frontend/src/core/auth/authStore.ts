import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';

interface AuthState {
  apiKey: string | null;
  setApiKey: (key: string) => void;
  logout: () => void;
}

export const HEADER_NAME = 'X-API-KEY';

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      apiKey: null,
      setApiKey: (key) => set({ apiKey: key }),
      logout: () => set({ apiKey: null }),
    }),
    {
      name: 'parcel-flow-auth',
      storage: createJSONStorage(() => AsyncStorage),
    }
  )
);
