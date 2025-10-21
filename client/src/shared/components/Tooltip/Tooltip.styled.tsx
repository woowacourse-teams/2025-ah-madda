import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

import { TooltipPlacement } from './Tooltip';

export const StyledTooltipContainer = styled.div`
  display: inline-flex;
  position: relative;
  align-items: center;
`;

export const PLACEMENT_TRANSFORMS: Record<TooltipPlacement, string> = {
  'top-left': 'translateY(-100%)',
  top: 'translateX(-50%) translateY(-100%)',
  'top-right': 'translateX(-100%) translateY(-100%)',
  'bottom-left': 'none',
  bottom: 'translateX(-50%)',
  'bottom-right': 'translateX(-100%)',
  'left-top': 'translateX(-100%) translateY(-100%)',
  left: 'translateX(-100%) translateY(-50%)',
  'left-bottom': 'translateX(-100%)',
  'right-top': 'translateY(-100%)',
  right: 'translateY(-50%)',
  'right-bottom': 'none',
} as const;

export const StyledTooltipContent = styled.div<{ placement: TooltipPlacement }>`
  position: fixed;
  padding: 8px 12px;
  border-radius: 6px;
  background-color: ${theme.colors.white};
  white-space: pre;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
  pointer-events: none;
  transition: opacity 0.15s ease-in-out;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.1),
    0 2px 4px rgba(0, 0, 0, 0.06);
  width: max-content;
  max-width: 500px;
  min-width: 200px;
  z-index: 1000;
  box-sizing: border-box;

  transform: ${({ placement }) => PLACEMENT_TRANSFORMS[placement]};

  @media (max-width: 768px) {
    padding: 6px 10px;
    width: max-content;
    box-sizing: border-box;
  }
`;
