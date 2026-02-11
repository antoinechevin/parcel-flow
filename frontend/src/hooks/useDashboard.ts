import { useState, useEffect } from 'react';
import * as Haptics from 'expo-haptics';
import { LocationGroup } from '../types';
import { useAuthStore, HEADER_NAME } from '../core/auth/authStore';
import { API_URL } from '../core/api/config';
import { MOCK_PARCELS } from '../core/api/__mocks__/mockData';

export const useDashboard = () => {
  const [groups, setGroups] = useState<LocationGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const apiKey = useAuthStore((state) => state.apiKey);
  const isDemoMode = useAuthStore((state) => state.isDemoMode);
  const logout = useAuthStore((state) => state.logout);

  const fetchParcels = async (silent = false) => {
    if (!silent) setLoading(true);
    
    if (isDemoMode) {
      setGroups(MOCK_PARCELS);
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(`${API_URL}/api/dashboard`, {
        headers: {
          [HEADER_NAME]: apiKey!,
        },
      });
      if (!response.ok) {
        if (response.status === 401) {
          logout();
          throw new Error('Session expirée ou clé invalide.');
        }
        throw new Error('Failed to fetch dashboard data');
      }
      const data = await response.json();
      setGroups(data);
    } catch (err: any) {
      setError(err.message);
    } finally {
      if (!silent) setLoading(false);
    }
  };

  useEffect(() => {
    if (!apiKey && !isDemoMode) {
      setLoading(false);
      return;
    }

    fetchParcels();
  }, [apiKey, isDemoMode]);

  const [undoTimeout, setUndoTimeout] = useState<NodeJS.Timeout | null>(null);
  const [pendingArchive, setPendingArchive] = useState<{trackingNumber: string, previousGroups: LocationGroup[]} | null>(null);

  useEffect(() => {
    // Cleanup timeout on unmount
    return () => {
      if (undoTimeout) clearTimeout(undoTimeout);
    };
  }, [undoTimeout]);

  const archiveParcel = async (trackingNumber: string) => {
    // If there's already a pending archive, execute it immediately
    if (undoTimeout && pendingArchive) {
      clearTimeout(undoTimeout);
      await executeArchive(pendingArchive.trackingNumber, pendingArchive.previousGroups);
    }

    const previousGroups = [...groups];
    setPendingArchive({ trackingNumber, previousGroups });

    // Optimistic UI update: remove the parcel from the local state immediately
    setGroups(currentGroups => 
      currentGroups.map(group => ({
        ...group,
        parcels: group.parcels.filter(p => p.trackingNumber !== trackingNumber)
      })).filter(group => group.parcels.length > 0)
    );

    const timeout = setTimeout(() => {
      executeArchive(trackingNumber, previousGroups);
      setUndoTimeout(null);
      setPendingArchive(null);
    }, 5000);

    setUndoTimeout(timeout);
  };

  const executeArchive = async (trackingNumber: string, previousGroupsFallback: LocationGroup[]) => {
    if (isDemoMode) {
        // In demo mode, we just simulate success (optimistic update stays)
        return;
    }

    try {
      const response = await fetch(`${API_URL}/api/parcels/${trackingNumber}/archive`, {
        method: 'POST',
        headers: {
          [HEADER_NAME]: apiKey!,
        },
      });
      if (!response.ok) {
        throw new Error('Échec de l\'archivage du colis');
      }
      // Silently refresh in the background to sync with server reality
      await fetchParcels(true);
    } catch (err: any) {
      // Rollback on error
      setGroups(previousGroupsFallback);
      setError(err.message);
    }
  };

  const undoArchive = () => {
    if (undoTimeout && pendingArchive) {
      Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);
      clearTimeout(undoTimeout);
      setGroups(pendingArchive.previousGroups);
      setUndoTimeout(null);
      setPendingArchive(null);
    }
  };

  return { 
    groups, 
    loading, 
    error, 
    archiveParcel, 
    undoArchive, 
    pendingTrackingNumber: pendingArchive?.trackingNumber,
    hasPendingArchive: !!undoTimeout, 
    refresh: () => fetchParcels(true) 
  };
};
