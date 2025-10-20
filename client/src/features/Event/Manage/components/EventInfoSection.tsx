import { css } from '@emotion/react';

import type { StatisticsAPIResponse } from '@/api/types/event';
import { Flex } from '@/shared/components/Flex';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';
import { formatDate } from '@/shared/utils/dateUtils';

import type { EventDetail } from '../../types/Event';
import { getEventCapacityInfo } from '../utils/eventCapacity';

import { Statistics } from './Statistics';

type EventInfoSectionProps = {
  event: EventDetail;
  statistics?: StatisticsAPIResponse[];
};

export const EventInfoSection = ({ event, statistics }: EventInfoSectionProps) => {
  const capacityInfo = getEventCapacityInfo(event.maxCapacity, event.currentGuestCount);

  return (
    <Flex as="section" dir="column" gap="40px" width="100%" padding="40px 0">
      <Flex dir="column" gap="12px">
        <Text type="Heading" weight="bold" color={theme.colors.gray800}>
          이벤트 소개
        </Text>
        <Text type="Body" weight="medium" color={theme.colors.gray800}>
          {event.description}
        </Text>
      </Flex>

      <Flex
        dir="row"
        gap="80px"
        alignItems="flex-start"
        css={css`
          @media (max-width: 768px) {
            flex-direction: column;
            gap: 40px;
          }
        `}
      >
        <Flex
          dir="column"
          gap="12px"
          css={css`
            flex: 1;
          `}
        >
          <Text type="Heading" weight="bold" color={theme.colors.gray800}>
            신청기간
          </Text>
          <Text type="Label" weight="medium" color={theme.colors.gray800}>
            {formatDate({
              start: event.registrationEnd,
              pattern: 'YYYY. MM. DD E A HH시',
              options: { dayOfWeek: 'long' },
            })}
            까지 신청가능
          </Text>
        </Flex>

        <Flex
          dir="column"
          gap="12px"
          css={css`
            flex: 1;
          `}
        >
          <Text type="Heading" weight="bold" color={theme.colors.gray800}>
            주최자
          </Text>
          <Text>{event.organizerNicknames}</Text>
        </Flex>
      </Flex>

      <Flex dir="column" gap="12px">
        <Text type="Heading" weight="bold" color={theme.colors.gray800}>
          참여 현황
        </Text>
        <Flex dir="row" gap="12px" alignItems="center" width="100%">
          <ProgressBar
            value={capacityInfo.progressValue}
            max={capacityInfo.progressMax}
            color={capacityInfo.progressColor}
            backgroundColor={theme.colors.gray100}
          />
          <Flex
            dir="row"
            gap="2px"
            alignItems="center"
            css={css`
              flex-shrink: 0;
            `}
          >
            <Text type="Body" weight="medium" color={theme.colors.gray600}>
              {event.currentGuestCount}
            </Text>
            <Text type="Body" weight="medium" color={theme.colors.gray600}>
              / {capacityInfo.maxNumberOfGuests}
            </Text>
          </Flex>
        </Flex>
      </Flex>

      <Statistics statistics={statistics ?? []} />
    </Flex>
  );
};
