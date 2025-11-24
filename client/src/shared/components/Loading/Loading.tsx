import { IconColor } from '@/shared/styles/colors';

import { Icon } from '../Icon';

import { StyledLoadingText, StyledLoadingSpinner } from './Loading.styled';

export type LoadingProps = {
  /**
   * The text to display in the loading component.
   * @type {string}
   * @default 'Loading'
   */
  type?: 'text' | 'spinner';
  /**
   * The size of the loading component.
   * @type {number}
   * @default 48
   */
  size?: number;
  /**
   * The color of the loading component.
   * @type {string}
   * @default 'white'
   */
  color?: IconColor;
};
export const Loading = ({ type = 'text', size = 48, color = 'white' }: LoadingProps) => {
  if (type === 'text') {
    const text = 'Loading';
    return (
      <span role="status" aria-live="polite" aria-label="Loading">
        {text.split('').map((char, i) => (
          <StyledLoadingText delay={i * 100} key={i} size={size}>
            {char}
          </StyledLoadingText>
        ))}
      </span>
    );
  }
  return (
    <StyledLoadingSpinner role="status" aria-live="polite" aria-label="Loading">
      <Icon name="loading" size={size} color={color} />
    </StyledLoadingSpinner>
  );
};
