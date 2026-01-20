// Mock Reanimated
require('react-native-reanimated').setUpTests();

// Mock Expo Router
jest.mock('expo-router', () => ({
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    back: jest.fn(),
  }),
  useSearchParams: () => ({}),
  useLocalSearchParams: () => ({}),
  Stack: {
    Screen: ({ options }) => null,
  },
}));

// Mock Expo Linking
jest.mock('expo-linking', () => ({
  createURL: (path) => path,
}));

// Mock Expo Constants
jest.mock('expo-constants', () => ({
  manifest: {
    extra: {},
  },
}));

// Mock React Native Paper
jest.mock('react-native-paper', () => {
  const React = require('react');
  const { View, Text } = require('react-native');
  return {
    Text: ({ children, ...props }) => <Text {...props}>{children}</Text>,
    Button: ({ children, onPress, ...props }) => (
      <View onTouchEnd={onPress} {...props}>
        <Text>{children}</Text>
      </View>
    ),
    Appbar: {
      Header: ({ children }) => <View>{children}</View>,
      Content: ({ title }) => <Text>{title}</Text>,
    },
  };
});

// Mock Expo Global Runtime
jest.mock('expo', () => ({
  registerRootComponent: jest.fn(),
}));
