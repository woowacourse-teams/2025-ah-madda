import { ReactNode } from 'react';

import styled from '@emotion/styled';

type Props = {
  children: ReactNode;
};
export const EventContainer = ({ children }: Props) => {
  return <Container>{children}</Container>;
};

const Container = styled.main`
  display: flex;
  flex-direction: column;
  gap: 40px;
  width: 100%;
  padding: 20px;
  background-color: rgba(231, 231, 231, 0.47);
`;
