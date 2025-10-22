import { useState, useRef, useEffect, useId, ReactNode } from 'react';

import { createPortal } from 'react-dom';

import { colorMap } from '@/shared/styles/colors';

import { useModal } from '../../hooks/useModal';

import {
  PLACEMENT_TRANSFORMS,
  StyledTooltipContainer,
  StyledTooltipContent,
} from './Tooltip.styled';

export type TooltipPlacement =
  | 'top-left'
  | 'top'
  | 'top-right'
  | 'bottom-left'
  | 'bottom'
  | 'bottom-right'
  | 'left-top'
  | 'left'
  | 'left-bottom'
  | 'right-top'
  | 'right'
  | 'right-bottom';

type TooltipProps = {
  /**
   * The content to display in the tooltip.
   */
  content: ReactNode | string;
  /**
   * The color of the tooltip.
   * @default 'gray'
   */
  color?: keyof typeof colorMap;
  /**
   * The placement of the tooltip relative to the trigger element.
   * @default 'top'
   */
  placement?: TooltipPlacement;
  /**
   * The children to display in the tooltip.
   */
  children: React.ReactNode;
};

const calculatePosition = (rect: DOMRect, placement: TooltipPlacement) => {
  const transform = PLACEMENT_TRANSFORMS[placement];
  return {
    top: rect.bottom + 8,
    left: rect.right,
    transform,
  };
};

export const Tooltip = ({ content, color = 'gray', placement = 'top', children }: TooltipProps) => {
  const tooltipId = useId();
  const { isOpen, open, close } = useModal();
  const [position, setPosition] = useState({ top: 0, left: 0 });
  const ref = useRef<HTMLDivElement>(null);

  const show = () => {
    if (ref.current) {
      const rect = ref.current.getBoundingClientRect();
      setPosition(calculatePosition(rect, placement));
    }
    open();
  };

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') close();
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [close]);

  return (
    <StyledTooltipContainer
      ref={ref}
      onMouseEnter={show}
      onMouseLeave={close}
      onFocus={show}
      onBlur={close}
      aria-describedby={isOpen ? tooltipId : undefined}
    >
      {children}
      {isOpen &&
        createPortal(
          <StyledTooltipContent
            id={tooltipId}
            role="tooltip"
            style={position}
            placement={placement}
          >
            {content}
          </StyledTooltipContent>,
          document.body
        )}
    </StyledTooltipContainer>
  );
};
