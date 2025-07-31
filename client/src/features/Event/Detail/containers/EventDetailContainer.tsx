import { PropsWithChildren } from 'react';

import { Flex } from '@/shared/components/Flex';

export const EventDetailContainer = ({ children }: PropsWithChildren) => {
  return (
    <Flex dir="column" gap="24px" margin="60px 0 0 0" padding="40px 20px 0 20px">
      {children}
    </Flex>
  );
};
