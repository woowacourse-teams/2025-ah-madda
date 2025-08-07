import styled from '@emotion/styled';

import { StatisticsAPIResponse } from '@/api/types/event';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { formatDate } from '../util/formatDate';

type ChartProps = {
  statistics: StatisticsAPIResponse[];
  maxViews: number;
  hoveredIndex: number | null;
  onChangeHover: (index: number | null) => void;
};

const gridLine = [0, 25, 50, 75, 100] as const;
export const Chart = ({ statistics, maxViews, hoveredIndex, onChangeHover }: ChartProps) => {
  return (
    <Flex dir="column">
      <ChartContainer>
        {gridLine.map((value) => (
          <GridLine key={value} top={value} />
        ))}
        {statistics.map((item, index) => {
          const height = (item.count / maxViews) * 200;
          const isHovered = hoveredIndex === index;

          return (
            <BarWrapper
              key={item.date}
              onMouseEnter={() => onChangeHover(index)}
              onMouseLeave={() => onChangeHover(null)}
            >
              {isHovered && (
                <TooltipContainer bottom={10}>
                  <Text type="Label" color={theme.colors.white} weight="semibold">
                    {item.count.toLocaleString()}회
                  </Text>
                </TooltipContainer>
              )}
              <Bar height={height} isHovered={isHovered} />
            </BarWrapper>
          );
        })}
      </ChartContainer>
      <XAxisLabels gap="4px" padding="20px 0 0 0">
        {statistics.map((item) => (
          <Text key={item.date} type="Body" weight="regular" color={theme.colors.black}>
            {formatDate(item.date)}
          </Text>
        ))}
      </XAxisLabels>
    </Flex>
  );
};

const XAxisLabels = styled(Flex)`
  > * {
    flex: 1;
    text-align: center;
  }
`;

const ChartContainer = styled(Flex)`
  position: relative;
  height: 200px;
  margin-top: 10px;
  overflow: visible;
`;

const GridLine = styled.div<{ top: number }>`
  position: absolute;
  top: ${({ top }) => `${top}%`};
  left: 0;
  right: 0;
  height: 1px;
  background-color: #f3f4f6;
  z-index: 1;
`;

const BarWrapper = styled(Flex)`
  position: relative;
  width: 10px;
  flex: 1;
  justify-content: center;
  align-items: flex-end;
  height: 100%;
`;

const Bar = styled.div<{ height: number; isHovered: boolean }>`
  width: 10px;
  z-index: 2;
  height: ${({ height }) => `${height}px`};
  background-color: ${({ isHovered }) =>
    isHovered ? `${theme.colors.primary300}` : `${theme.colors.primary100}`};
  border-radius: 4px 4px 0 0;
  transition: all 0.2s ease;
  cursor: pointer;
  transform: ${({ isHovered }) => (isHovered ? 'scale(1.05)' : 'scale(1)')};
  box-shadow: ${({ isHovered }) => (isHovered ? '0 4px 12px rgba(59, 130, 246, 0.3)' : 'none')};
`;

const TooltipContainer = styled.div<{ bottom: number }>`
  position: absolute;
  bottom: ${({ bottom }) => `${bottom}px`};
  left: 50%;
  transform: translateX(-50%);
  background-color: ${theme.colors.primary600};
  padding: 12px;
  border-radius: 6px;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  pointer-events: none;
`;
