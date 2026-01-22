import { useState, useEffect } from 'react';
import { Parcel } from '../components/ParcelCard';

const API_URL = 'http://localhost:8080/api/parcels';

export const useParcels = () => {
  const [parcels, setParcels] = useState<Parcel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchParcels = async () => {
      try {
        const response = await fetch(API_URL);
        if (!response.ok) {
          throw new Error('Failed to fetch parcels');
        }
        const data = await response.json();
        setParcels(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchParcels();
  }, []);

  return { parcels, loading, error };
};
