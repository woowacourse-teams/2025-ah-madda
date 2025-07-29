import { ReactNode } from 'react';

import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';

type MyEventContainerProps = {
  children: ReactNode;
};

export const MyEventContainer = ({ children }: MyEventContainerProps) => {
  return (
    <Flex
      dir="column"
      margin="0 10px"
      padding="60px 0 0 0"
      css={css`
        @media (max-width: 768px) {
          align-items: center;
        }
      `}
    >
      {children}
    </Flex>
  );
};
