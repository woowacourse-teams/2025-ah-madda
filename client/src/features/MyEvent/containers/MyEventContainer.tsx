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
      css={css`
        padding-top: 60px;
        @media (max-width: 768px) {
          align-items: center;
        }
      `}
    >
      {children}
    </Flex>
  );
};
