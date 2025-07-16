import React from 'react';
import { ComponentProps } from 'react';

import { ProgressContainer, ProgressFill } from './ProgressBar.styled';

/**
 * Props for the ProgressBar component.
 */
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
   * @default 100
   */
  max?: number;

  /**
   * The height of the progress bar.
   * @type {string}
   * @example '8px'
   * @default '8px'
   */
  height?: string;

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
   * The border-radius applied to both the bar and its container.
   * @type {string}
   * @example '16px'
   * @default '16px'
   */
  borderRadius?: string;

  /**
   * Whether to animate the width transition of the progress bar.
   * @type {boolean}
   * @default true
   */
  animated?: boolean;
} & ComponentProps<'div'>;

export const ProgressBar: React.FC<ProgressBarProps> = ({
  value,
  max = 100,
  height = '8px',
  color = '#409869',
  backgroundColor = '#e2e2e2',
  borderRadius = '16px',
  animated = true,
}) => {
  const percentage = Math.min(Math.max((value / max) * 100, 0), 100);

  return (
    <ProgressContainer
      height={height}
      backgroundColor={backgroundColor}
      borderRadius={borderRadius}
      aria-label={`Progress: ${percentage}%`}
    >
      <ProgressFill
        width={percentage}
        color={color}
        borderRadius={borderRadius}
        animated={animated}
      />
    </ProgressContainer>
  );
};
