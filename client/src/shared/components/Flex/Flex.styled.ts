import styled from '@emotion/styled';

export type FlexStyle = {
  direction?: 'row' | 'column';

  align?:
    | 'normal'
    | 'normal'
    | 'stretch'
    | 'center'
    | 'start'
    | 'end'
    | 'flex-start'
    | 'flex-end'
    | 'self-start'
    | 'self-end'
    | 'baseline'
    | 'inherit'
    | 'initial'
    | 'unset';

  justify?:
    | 'center'
    | 'start'
    | 'flex-start'
    | 'end'
    | 'flex-end'
    | 'left'
    | 'right'
    | 'normal'
    | 'space-between'
    | 'space-around'
    | 'space-evenly'
    | 'stretch'
    | 'inherit'
    | 'initial'
    | 'revert'
    | 'unset';

  gap?: string;

  wrap?: 'nowrap' | 'wrap' | 'wrap-reverse' | 'inherit' | 'initial';

  basis?: 0 | 'auto' | '200px';

  grow?: string;

  shrink?: number;

  position?: 'static' | 'absolute' | 'relative' | 'fixed' | 'sticky' | 'inherit';

  top?: string;

  right?: string;

  bottom?: string;

  left?: string;

  width?: string;

  height?: string;

  maxWidth?: string;

  maxHeight?: string;

  margin?: string;

  marginRight?: string;

  marginTop?: string;

  marginLeft?: string;

  marginBottom?: string;

  padding?: string;

  paddingTop?: string;

  paddingRight?: string;

  paddingBottom?: string;

  paddingLeft?: string;
};

export const StyledFlex = styled.div<FlexStyle>`
  display: flex;

  position: ${({ position = 'static' }) => position};

  top: ${({ top = '' }) => top};

  right: ${({ right = '' }) => right};

  bottom: ${({ bottom = '' }) => bottom};

  left: ${({ left = '' }) => left};

  flex-wrap: ${({ wrap = 'nowrap' }) => wrap};

  flex-basis: ${({ basis = 'auto' }) => basis};

  flex-grow: ${({ grow = '0' }) => grow};

  flex-shrink: ${({ shrink = 0 }) => shrink};

  flex-direction: ${({ direction = 'row' }) => direction};

  align-items: ${({ align = 'flex-start' }) => align};

  justify-content: ${({ justify = 'flex-start' }) => justify};

  gap: ${({ gap = '0px' }) => gap};

  width: ${({ width = '' }) => width};

  height: ${({ height = '' }) => height};

  max-width: ${({ maxWidth = '' }) => maxWidth};

  max-height: ${({ maxHeight = '' }) => maxHeight};

  margin: ${({ margin = '0' }) => margin};

  margin-right: ${({ marginRight = '' }) => marginRight};

  margin-left: ${({ marginLeft = '' }) => marginLeft};

  margin-top: ${({ marginTop = '' }) => marginTop};

  margin-bottom: ${({ marginBottom = '' }) => marginBottom};

  padding: ${({ padding = '' }) => padding};

  padding-top: ${({ paddingTop = '' }) => paddingTop};

  padding-right: ${({ paddingRight = '' }) => paddingRight};

  padding-bottom: ${({ paddingBottom = '' }) => paddingBottom};

  padding-left: ${({ paddingLeft = '' }) => paddingLeft};
`;
