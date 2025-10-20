import { ReactNode } from 'react';

import { css } from '@emotion/react';

import { Flex } from '../Flex';
import { Footer } from '../Footer';

import { PageHeader } from './PageHeader';

type PageLayoutProps = {
  /**
   * Main content of the page layout.
   */
  children: ReactNode;
};

export const PageLayout = ({ children }: PageLayoutProps) => {
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
      <PageHeader />
      <Flex
        as="main"
        dir="column"
        width="100%"
        css={css`
          flex-grow: 1;
          max-width: 1120px;
        `}
      >
        {children}
      </Flex>
      <Footer />
    </Flex>
  );
};
