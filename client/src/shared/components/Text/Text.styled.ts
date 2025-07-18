import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { TextElementType, TextProps } from './Text';

const fontTypes = {
  Head: css`
    font-size: 2rem;
  `,
  Title: css`
    font-size: 1.5rem;
  `,
  Body: css`
    font-size: 1.25rem;
  `,
  caption: css`
    font-size: 1rem;
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
