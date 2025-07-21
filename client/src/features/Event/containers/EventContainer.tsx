import { ReactNode } from 'react';

import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';

type Props = {
  children: ReactNode;
};
export const EventContainer = ({ children }: Props) => {
  return (
    <Flex
      as="main"
      dir="column"
      width="100%"
      gap="40px"
      padding="20px"
      css={css`
        background-color: rgba(231, 231, 231, 0.47);
        border-radius: 10px;
      `}
    >
      {children}
    </Flex>
  );
};
