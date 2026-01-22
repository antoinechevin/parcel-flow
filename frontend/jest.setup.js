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

// Mock Expo Constants
jest.mock('expo-constants', () => ({
  default: {
    manifest: {
      extra: {},
    },
  },
}));

// Mock Expo Linking
jest.mock('expo-linking', () => ({
  createURL: (path) => path,
}));