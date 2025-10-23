import { PropsWithChildren } from 'react';

import { A11y } from '@/shared/components/A11y/A11y';
import { Flex } from '@/shared/components/Flex';

export const EventDetailContainer = ({
  children,
  introDesc,
}: PropsWithChildren<{ introDesc?: string }>) => {
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
      <A11y introDesc={introDesc} />
      {children}
    </Flex>
  );
};
