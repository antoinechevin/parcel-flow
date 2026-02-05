// Mock Reanimated
jest.mock('react-native-reanimated', () => {
  const React = require('react');
  const View = require('react-native').View;
  return {
    default: {
      call: () => {},
    },
    useSharedValue: (v) => ({ value: v }),
    useAnimatedStyle: (fn) => fn(),
    withTiming: (v) => v,
    withSpring: (v) => v,
    runOnJS: (fn) => fn,
    runOnUI: (fn) => fn,
    makeMutable: (v) => ({ value: v }),
    setUpTests: () => {},
    View: View,
  };
});

// Mock Worklets
jest.mock('react-native-worklets-core', () => ({
  Worklets: {
    createRunOnJS: (fn) => fn,
    createRunOnUI: (fn) => fn,
  },
}));

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
