import React from 'react';
import renderer from 'react-test-renderer';
import Heartbeat from './components/Heartbeat';

describe('Walking Skeleton', () => {
  it('renders Heartbeat component correctly', () => {
    const tree = renderer.create(<Heartbeat />).toJSON();
    expect(tree).toBeDefined();
    // Verify meaningful content exists
    expect(JSON.stringify(tree)).toContain('Walking Skeleton');
  });
});
