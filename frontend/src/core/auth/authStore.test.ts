import { renderHook, act } from '@testing-library/react-native';
import { useAuthStore } from './authStore';

describe('authStore', () => {
  beforeEach(() => {
    const { result } = renderHook(() => useAuthStore());
    act(() => {
      result.current.logout();
      if (result.current.setDemoMode) {
        result.current.setDemoMode(false);
      }
    });
  });

  it('should toggle demo mode', () => {
    const { result } = renderHook(() => useAuthStore());

    // Initially undefined or false
    expect(result.current.isDemoMode).toBe(false);

    act(() => {
      result.current.setDemoMode(true);
    });

    expect(result.current.isDemoMode).toBe(true);

    act(() => {
      result.current.setDemoMode(false);
    });

    expect(result.current.isDemoMode).toBe(false);
  });
});
