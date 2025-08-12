import { ReactNode } from 'react';

import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

type QuestionContainerProps = {
  children: ReactNode;
};
export const QuestionContainer = ({ children }: QuestionContainerProps) => {
  return <StyledQuestionContainer>{children}</StyledQuestionContainer>;
};

const StyledQuestionContainer = styled.section`
  margin: 0 12px;
  padding: 32px;
  background-color: ${theme.colors.gray50};
  border-radius: 4px;
`;
