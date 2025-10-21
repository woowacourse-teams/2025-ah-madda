import { PropsWithChildren } from 'react';

import { Flex } from '@/shared/components/Flex';

export const EventDetailContainer = ({ children }: PropsWithChildren) => {
  return (
    <Flex
      as="main"
      id="event-main"
      role="main"
      aria-labelledby="event-title"
      dir="column"
      gap="24px"
      margin="60px 0 0 0"
      padding="40px 20px 0 20px"
      style={{ position: 'relative' }}
    >
      <div
        id="a11y-live-1"
        role="status"
        aria-live="assertive"
        aria-atomic="true"
        style={{
          position: 'absolute',
          width: 1,
          height: 1,
          margin: -1,
          padding: 0,
          border: 0,
          clip: 'rect(0 0 0 0)',
          overflow: 'hidden',
        }}
      />
      <div
        id="a11y-live-2"
        role="status"
        aria-live="assertive"
        aria-atomic="true"
        style={{
          position: 'absolute',
          width: 1,
          height: 1,
          margin: -1,
          padding: 0,
          border: 0,
          clip: 'rect(0 0 0 0)',
          overflow: 'hidden',
        }}
      />

      {children}
    </Flex>
  );
};
