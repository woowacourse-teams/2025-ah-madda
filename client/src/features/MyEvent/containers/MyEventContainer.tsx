import { ReactNode } from 'react';

import { Flex } from '@/shared/components/Flex';

type MyEventContainerProps = {
  children: ReactNode;
};

export const MyEventContainer = ({ children }: MyEventContainerProps) => {
  return <Flex dir="column">{children}</Flex>;
};
