import React from 'react';
import { ComponentProps } from 'react';

import { StyledProgressContainer, StyledProgressFill } from './ProgressBar.styled';

export type ProgressBarProps = {
  /**
   * The current progress value.
   * @type {number}
   * @example 50
   */
  value: number;

  /**
   * The maximum value the progress can reach.
   * @type {number}
   * @example 100
   */
  max: number;

  /**
   * The fill color of the progress bar.
   * @type {string}
   * @example '#409869'
   * @default '#409869'
   */
  color?: string;

  /**
   * The background color of the progress bar container.
   * @type {string}
   * @example '#e2e2e2'
   * @default '#e2e2e2'
   */
  backgroundColor?: string;

  /**
   * Whether to animate the width transition of the progress bar.
   * @type {boolean}
   * @default true
   */
  animated?: boolean;
} & ComponentProps<'div'>;

export const ProgressBar = ({
  value,
  max,
  color = '#409869',
  backgroundColor = '#e2e2e2',
  animated = true,
}: ProgressBarProps) => {
  const percentage = Math.min(Math.max((value / max) * 100, 0), 100);

  return (
    <StyledProgressContainer
      backgroundColor={backgroundColor}
      aria-label={`Progress: ${percentage}%`}
    >
      <StyledProgressFill percentage={percentage} color={color} animated={animated} />
    </StyledProgressContainer>
  );
};
