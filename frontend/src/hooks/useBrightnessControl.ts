import { useEffect, useRef, useState } from 'react';
import * as Brightness from 'expo-brightness';

export const useBrightnessControl = (isActive: boolean) => {
  const originalBrightness = useRef<number | null>(null);
  const [permissionGranted, setPermissionGranted] = useState<boolean | null>(null);

  useEffect(() => {
    let isMounted = true;

    const manageBrightness = async () => {
      if (isActive) {
        try {
          // Only request if we haven't determined status yet
          if (permissionGranted === null) {
            const { status } = await Brightness.requestPermissionsAsync();
            if (isMounted) setPermissionGranted(status === 'granted');
            if (status !== 'granted') return;
          } else if (!permissionGranted) {
            return;
          }

          // Save and set brightness
          const current = await Brightness.getBrightnessAsync();
          if (isMounted) {
            originalBrightness.current = current;
            await Brightness.setBrightnessAsync(1);
          }
        } catch (error) {
          console.warn('useBrightnessControl: Could not manage brightness:', error);
        }
      } else if (originalBrightness.current !== null) {
        // Restore brightness
        await Brightness.setBrightnessAsync(originalBrightness.current);
        originalBrightness.current = null;
      }
    };

    manageBrightness();

    return () => {
      isMounted = false;
      if (originalBrightness.current !== null) {
        Brightness.setBrightnessAsync(originalBrightness.current).catch(err =>
          console.warn('useBrightnessControl: Failed to restore brightness on cleanup', err)
        );
      }
    };
  }, [isActive, permissionGranted]);

  return { permissionGranted };
};
