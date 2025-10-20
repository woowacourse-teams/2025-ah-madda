import { css } from '@emotion/react';

import type { StatisticsAPIResponse } from '@/api/types/event';
import { calculateCapacityStatus } from '@/features/Event/utils/calculateCapacityStatus';
import { Flex } from '@/shared/components/Flex';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';
import { formatDate } from '@/shared/utils/dateUtils';

import type { EventDetail } from '../../../types/Event';

import { Statistics } from './Statistics';

type EventInfoSectionProps = {
  event: EventDetail;
  statistics?: StatisticsAPIResponse[];
};

export const EventInfoSection = ({ event, statistics }: EventInfoSectionProps) => {
  const { isUnlimited, progressValue, progressMax } = calculateCapacityStatus(
    event.maxCapacity,
    event.currentGuestCount
  );
  return (
    <Flex as="section" dir="column" gap="36px" margin="40px 0" padding="0 16px">
      <Flex dir="column" alignItems="flex-start" gap="12px">
        <Text as="h2" type="Heading" weight="semibold">
          이벤트 소개
        </Text>
        <Text>{event.description}</Text>
      </Flex>

      <Flex
        dir="row"
        justifyContent="space-around"
        alignItems="flex-start"
        css={css`
          @media (max-width: 768px) {
            flex-direction: column;
            gap: 36px;
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
          <Text as="h2" type="Heading" weight="semibold">
            신청기간
          </Text>
          <Text>
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
          <Text>
            {event.organizerNicknames.length <= 3
              ? event.organizerNicknames.join(', ')
              : `${event.organizerNicknames.slice(0, 3).join(', ')} 외 ${event.organizerNicknames.length - 3}명`}
          </Text>
        </Flex>
      </Flex>

      <Flex width="100%" dir="column" alignItems="flex-start" gap="12px">
        <Text type="Heading" weight="bold" color={theme.colors.gray800}>
          참여 현황
        </Text>
        <Flex dir="row" gap="12px" alignItems="center" width="100%">
          <ProgressBar value={progressValue} max={progressMax} color={theme.colors.primary500} />
          <Flex width="20%" justifyContent="center" alignItems="flex-end">
            <Text type="Label" color={theme.colors.gray500} weight="semibold">
              {isUnlimited ? '무제한' : `${event.currentGuestCount} / ${event.maxCapacity}`}
            </Text>
          </Flex>
        </Flex>
      </Flex>

      <Statistics statistics={statistics ?? []} />
    </Flex>
  );
};
