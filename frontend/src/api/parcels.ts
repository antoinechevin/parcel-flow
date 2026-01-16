import axios from 'axios';
import { Platform } from 'react-native';

const API_URL = Platform.select({
  android: 'http://10.0.2.2:8080/api',
  ios: 'http://localhost:8080/api',
  default: 'http://localhost:8080/api',
});

export const api = axios.create({
  baseURL: API_URL,
});

export interface Parcel {
  id: string;
  label: string;
  status: string;
}

export const fetchParcels = async (): Promise<Parcel[]> => {
  const response = await api.get<Parcel[]>('/parcels');
  return response.data;
};

export const createParcel = async (id: string, label: string): Promise<Parcel> => {
  const response = await api.post<Parcel>('/parcels', { id, label });
  return response.data;
};
