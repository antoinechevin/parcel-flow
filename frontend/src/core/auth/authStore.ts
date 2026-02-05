import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';

interface AuthState {
  apiKey: string | null;
  isDemoMode: boolean;
  setApiKey: (key: string) => void;
  setDemoMode: (isDemo: boolean) => void;
  logout: () => void;
}

export const HEADER_NAME = 'X-API-KEY';

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      apiKey: null,
      isDemoMode: false,
      setApiKey: (key) => set({ apiKey: key }),
      setDemoMode: (isDemo) => set({ isDemoMode: isDemo }),
      logout: () => set({ apiKey: null, isDemoMode: false }),
    }),
    {
      name: 'parcel-flow-auth',
      storage: createJSONStorage(() => AsyncStorage),
    }
  )
);
