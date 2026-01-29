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



// Mock react-native-qrcode-svg

jest.mock('react-native-qrcode-svg', () => {

  const React = require('react');

  return (props) => React.createElement('View', props);

});



// Mock react-native-svg

jest.mock('react-native-svg', () => {

  const React = require('react');

  return {

    Svg: (props) => React.createElement('View', props),

    Path: (props) => React.createElement('View', props),

    G: (props) => React.createElement('View', props),

    Rect: (props) => React.createElement('View', props),

  };

});
