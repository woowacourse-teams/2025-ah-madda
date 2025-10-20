import styled from '@emotion/styled';

import { StatisticsAPIResponse } from '@/api/types/event';
import { Flex } from '@/shared/components/Flex';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { useHoverIndex } from '../../hooks/useHoverIndex';

import { Chart } from './Chart';

type StatisticsProps = {
  statistics: StatisticsAPIResponse[];
};

export const Statistics = ({ statistics }: StatisticsProps) => {
  const { hoveredIndex, handleChangeHover } = useHoverIndex();
  const maxViews = Math.max(...statistics.map((item) => item.count));
  const totalCount = statistics.reduce((acc, item) => acc + item.count, 0);

  return (
    <Flex as="section" dir="column">
      <Flex dir="column" gap="20px">
        <Text type="Heading" weight="bold" color={theme.colors.gray800}>
          이벤트 조회수
        </Text>
        <Flex dir="column" alignItems="flex-start">
          <Text type="Label" weight="medium" color={theme.colors.gray600}>
            Total
          </Text>
          <Text type="Heading" weight="medium" color={theme.colors.gray900}>
            {totalCount}
          </Text>
        </Flex>
      </Flex>
      <Spacing height="20px" />
      <ChartContainer>
        <YAxisLabels
          dir="column"
          justifyContent="space-between"
          alignItems="center"
          padding="0 4px 0 0"
          gap="27px"
        >
          {[
            maxViews,
            Math.round(maxViews * 0.75),
            Math.round(maxViews * 0.5),
            Math.round(maxViews * 0.25),
            0,
          ].map((value, index) => (
            <Text key={index} type="Body" weight="regular" color={theme.colors.primary600}>
              {value}
            </Text>
          ))}
        </YAxisLabels>
        <ChartWrapper>
          <Chart
            statistics={statistics}
            maxViews={maxViews}
            hoveredIndex={hoveredIndex}
            onChangeHover={handleChangeHover}
          />
        </ChartWrapper>
      </ChartContainer>
    </Flex>
  );
};

const ChartContainer = styled(Flex)`
  position: relative;
  width: 100%;
  height: auto;
  overflow: hidden;
`;

const ChartWrapper = styled.div`
  margin-left: 40px;
  flex: 8;
`;

const YAxisLabels = styled(Flex)`
  position: absolute;
  left: 0;
  top: 0;
  height: 200px;
  flex: 1;
`;
