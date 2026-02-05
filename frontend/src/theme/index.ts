import { MD3LightTheme } from 'react-native-paper';

export const AppTheme = {
  ...MD3LightTheme,
  colors: {
    ...MD3LightTheme.colors,
    // Custom semantic colors
    warning: '#FFB300', // Orange for MEDIUM urgency
    info: '#2196F3',    // Blue for LOW urgency
    
    // Explicit overrides for high contrast
    snackbarBackground: '#1C1B1F', // Dark background
    snackbarText: '#F4EFF4',       // Light text
    snackbarAction: '#bb86fc',     // Light purple for action
    
    swipeActionBackground: '#B00020', // Error red for archive/delete
    swipeActionText: '#ffffff',
  },
};
