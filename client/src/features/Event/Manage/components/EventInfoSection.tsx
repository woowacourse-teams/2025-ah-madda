import { css } from '@emotion/react';

import type { StatisticsAPIResponse } from '@/api/types/event';
import type { Profile } from '@/api/types/profile';
import { Avatar } from '@/shared/components/Avatar';
import { Flex } from '@/shared/components/Flex';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { formatDateTime } from '../../My/utils/date';
import { UNLIMITED_CAPACITY } from '../../New/constants/errorMessages';
import type { Event } from '../../types/Event';

import { Statistics } from './Statistics';

type EventInfoSectionProps = {
  event: Event;
  profile?: Profile;
  statistics?: StatisticsAPIResponse[];
};

export const EventInfoSection = ({ event, profile, statistics }: EventInfoSectionProps) => {
  const isUnlimited = event.maxCapacity === UNLIMITED_CAPACITY;
  const maxNumberOfGuests = isUnlimited ? '제한없음' : `${event.maxCapacity}명`;
  const progressValue = isUnlimited ? 1 : Number(event.currentGuestCount);
  const progressMax = isUnlimited ? 1 : event.maxCapacity;
  const progressColor = isUnlimited ? theme.colors.primary700 : theme.colors.primary500;

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

      <Flex dir="row" gap="80px" alignItems="flex-start">
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
            {formatDateTime(event.registrationEnd)} 까지
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
          <Avatar picture={profile?.picture ?? ''} name={profile?.name ?? ''} />
        </Flex>
      </Flex>

      <Flex dir="column" gap="12px">
        <Text type="Heading" weight="bold" color={theme.colors.gray800}>
          참여 현황
        </Text>
        <Flex dir="row" gap="12px" alignItems="center" width="100%">
          <ProgressBar
            value={progressValue}
            max={progressMax}
            color={progressColor}
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
            <Text type="Body" weight="medium" color={theme.colors.gray400}>
              /{maxNumberOfGuests}
            </Text>
          </Flex>
        </Flex>
      </Flex>

      <Statistics statistics={statistics ?? []} />
    </Flex>
  );
};
