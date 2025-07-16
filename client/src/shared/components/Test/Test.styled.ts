import styled from '@emotion/styled';

type StyledTestProps = {
  variant: 'primary' | 'secondary';
};

export const StyledTest = styled.div<StyledTestProps>`
  padding: 16px;
  border-radius: 8px;
  border: 2px solid;
  margin: 8px 0;

  ${({ variant }) =>
    variant === 'primary'
      ? `
        background-color: #e3f2fd;
        border-color: #2196f3;
        color: #1976d2;
      `
      : `
        background-color: #f3e5f5;
        border-color: #9c27b0;
        color: #7b1fa2;
      `}

  h3 {
    margin: 0 0 8px 0;
    font-size: 18px;
    font-weight: 600;
  }

  div {
    font-size: 14px;
    line-height: 1.4;
  }
`;
