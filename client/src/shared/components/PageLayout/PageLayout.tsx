import { ReactNode } from 'react';

import { css } from '@emotion/react';

import { Flex } from '../Flex';

type PageLayoutProps = {
  /**
   * Header content to be displayed at the top of the page layout.
   */
  header: ReactNode;
  /**
   * Main content of the page layout.
   */
  children: ReactNode;
};

export const PageLayout = ({ header, children }: PageLayoutProps) => {
  return (
    <Flex
      dir="column"
      alignItems="center"
      justifyContent="space-between"
      width="100%"
      height="100%"
      css={css`
        min-height: 100vh;
      `}
    >
      {header}
      <Flex
        dir="column"
        width="100%"
        css={css`
          max-width: 1120px;
        `}
      >
        {children}
      </Flex>
    </Flex>
  );
};
