import { useState, useEffect } from 'react';
import { LocationGroup } from '../types';

const API_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';

export const useDashboard = () => {
  const [groups, setGroups] = useState<LocationGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchParcels = async () => {
      try {
        const response = await fetch(`${API_URL}/api/dashboard`);
        if (!response.ok) {
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
  }, []);

  return { groups, loading, error };
};