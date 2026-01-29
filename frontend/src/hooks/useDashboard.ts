import { useState, useEffect } from 'react';
import { LocationGroup } from '../types';
import { useAuthStore, HEADER_NAME } from '../core/auth/authStore';

const API_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';

export const useDashboard = () => {
  const [groups, setGroups] = useState<LocationGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const apiKey = useAuthStore((state) => state.apiKey);
  const logout = useAuthStore((state) => state.logout);

  useEffect(() => {
    if (!apiKey) {
      setLoading(false);
      return;
    }

    const fetchParcels = async () => {
      try {
        const response = await fetch(`${API_URL}/api/dashboard`, {
          headers: {
            [HEADER_NAME]: apiKey,
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
        setLoading(false);
      }
    };

    fetchParcels();
  }, [apiKey]);

  return { groups, loading, error };
};