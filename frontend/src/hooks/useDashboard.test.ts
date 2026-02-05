import { renderHook, act } from '@testing-library/react-native';
import { useDashboard } from './useDashboard';
import { useAuthStore } from '../core/auth/authStore';

// Mock auth store
jest.mock('../core/auth/authStore', () => ({
  useAuthStore: jest.fn(),
  HEADER_NAME: 'X-API-KEY',
}));

// Mock fetch
global.fetch = jest.fn() as jest.Mock;

describe('useDashboard', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
    (useAuthStore as unknown as jest.Mock).mockReturnValue('test-api-key');
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('delays archive call and allows undo', async () => {
    const mockParcels = [
      { trackingNumber: '123', pickupPoint: { id: 'pp1', name: 'Test' }, deadline: '2026-01-01', status: 'AVAILABLE' }
    ];
    const mockGroups = [{ pickupPoint: { id: 'pp1', name: 'Test' }, parcels: mockParcels }];

    (global.fetch as jest.Mock).mockResolvedValueOnce({
      ok: true,
      json: async () => mockGroups,
    });

    const { result } = renderHook(() => useDashboard());

    // Initial fetch
    await act(async () => {
      jest.advanceTimersByTime(0);
    });

    expect(result.current.groups).toHaveLength(1);

    // Call archive
    await act(async () => {
      result.current.archiveParcel('123');
    });

    // Optimistic update
    expect(result.current.groups).toHaveLength(0);
    expect(result.current.hasPendingArchive).toBe(true);
    expect(result.current.pendingTrackingNumber).toBe('123');
    expect(global.fetch).not.toHaveBeenCalledWith(expect.stringContaining('archive'), expect.anything());

    // Undo
    act(() => {
      result.current.undoArchive();
    });

    expect(result.current.groups).toHaveLength(1);
    expect(result.current.hasPendingArchive).toBe(false);
    expect(result.current.pendingTrackingNumber).toBeUndefined();

    // Ensure no API call after timeout
    act(() => {
      jest.advanceTimersByTime(5000);
    });
    expect(global.fetch).not.toHaveBeenCalledWith(expect.stringContaining('archive'), expect.anything());
  });

  it('handles race conditions by executing previous archive immediately', async () => {
    const mockGroups = [
      { pickupPoint: { id: 'pp1', name: 'Test' }, parcels: [{ trackingNumber: '123' }, { trackingNumber: '456' }] }
    ] as any;

    (global.fetch as jest.Mock).mockResolvedValue({ ok: true, json: async () => [] });

    const { result } = renderHook(() => useDashboard());

    await act(async () => {
      result.current.archiveParcel('123');
    });

    expect(result.current.pendingTrackingNumber).toBe('123');

    // Archive another one immediately
    await act(async () => {
      result.current.archiveParcel('456');
    });

    // Should have called executeArchive for '123' immediately
    expect(global.fetch).toHaveBeenCalledWith(expect.stringContaining('123/archive'), expect.anything());
    expect(result.current.pendingTrackingNumber).toBe('456');
  });

  it('executes archive call after timeout if not undone', async () => {
    const mockParcels = [
      { trackingNumber: '123', pickupPoint: { id: 'pp1', name: 'Test' }, deadline: '2026-01-01', status: 'AVAILABLE' }
    ];
    const mockGroups = [{ pickupPoint: { id: 'pp1', name: 'Test' }, parcels: mockParcels }];

    (global.fetch as jest.Mock).mockResolvedValueOnce({
      ok: true,
      json: async () => mockGroups,
    });

    const { result } = renderHook(() => useDashboard());

    await act(async () => {
      result.current.archiveParcel('123');
    });

    (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: true }); // Mock archive response
    (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: true, json: async () => [] }); // Mock refresh response

    // Wait for timeout
    await act(async () => {
      jest.advanceTimersByTime(5000);
    });

    expect(global.fetch).toHaveBeenCalledWith(expect.stringContaining('archive'), expect.objectContaining({ method: 'POST' }));
    expect(result.current.hasPendingArchive).toBe(false);
  });
});
