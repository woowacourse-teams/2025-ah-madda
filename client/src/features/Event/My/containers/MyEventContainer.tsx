import { ReactNode } from 'react';

import { Flex } from '@/shared/components/Flex';

type MyEventContainerProps = {
  children: ReactNode;
};

export const MyEventContainer = ({ children }: MyEventContainerProps) => {
  return (
    <Flex dir="column" gap="24px" margin="60px 0 0 0" padding="40px 20px 0 20px">
      {children}
    </Flex>
  );
};
