import { PropsWithChildren } from 'react';

import { Flex } from '@/shared/components/Flex';

export const ErrorContainer = ({ children }: PropsWithChildren) => {
  return (
    <Flex
      dir="column"
      gap="24px"
      height="100vh"
      justifyContent="center"
      alignItems="center"
      padding="40px 20px"
    >
      {children}
    </Flex>
  );
};
