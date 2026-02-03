import { useState, useEffect } from 'react';
import { LocationGroup } from '../types';
import { useAuthStore, HEADER_NAME } from '../core/auth/authStore';
import { API_URL } from '../core/api/config';

export const useDashboard = () => {
  const [groups, setGroups] = useState<LocationGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const apiKey = useAuthStore((state) => state.apiKey);
  const logout = useAuthStore((state) => state.logout);

  const fetchParcels = async (silent = false) => {
    if (!silent) setLoading(true);
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
    if (!apiKey) {
      setLoading(false);
      return;
    }

    fetchParcels();
  }, [apiKey]);

  const archiveParcel = async (trackingNumber: string) => {
    // Optimistic UI update: remove the parcel from the local state immediately
    const previousGroups = [...groups];
    setGroups(currentGroups => 
      currentGroups.map(group => ({
        ...group,
        parcels: group.parcels.filter(p => p.trackingNumber !== trackingNumber)
      })).filter(group => group.parcels.length > 0)
    );

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
      setGroups(previousGroups);
      setError(err.message);
    }
  };

  return { groups, loading, error, archiveParcel, refresh: () => fetchParcels(true) };
};
