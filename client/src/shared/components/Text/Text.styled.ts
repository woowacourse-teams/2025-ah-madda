import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { TextElementType, TextProps } from './Text';

const fontTypes = {
  Display: css`
    font-size: 2.25rem;

    @media (max-width: 768px) {
      font-size: 2rem;
    }
  `,
  Title: css`
    font-size: 1.75rem;

    @media (max-width: 768px) {
      font-size: 1.5rem;
    }
  `,
  Heading: css`
    font-size: 1.25rem;

    @media (max-width: 768px) {
      font-size: 1.025rem;
    }
  `,
  Body: css`
    font-size: 1rem;
  `,
  Label: css`
    font-size: 0.875rem;
  `,
};

const fontWeights = {
  regular: css`
    font-weight: 400;
  `,
  medium: css`
    font-weight: 500;
  `,
  semibold: css`
    font-weight: 600;
  `,
  bold: css`
    font-weight: 700;
  `,
};

export const StyledText = styled.p<TextProps<TextElementType>>`
  white-space: pre-wrap;
  line-height: 140%;
  margin: 0;

  color: ${({ color }) => color};
  ${({ type }) => fontTypes[type ?? 'Body']}
  ${({ weight }) => fontWeights[weight ?? 'regular']};
`;
