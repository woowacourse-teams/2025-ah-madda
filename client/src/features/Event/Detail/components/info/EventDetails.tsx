import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { calculateCapacityStatus } from '../../../Overview/utils/calculateCapacityStatus';
import { EventDetail } from '../../../types/Event';
import { formatKoreanDateTime } from '../../utils/formatKoreanDateTime';

type EventDetailProps = Pick<
  EventDetail,
  'organizerName' | 'description' | 'currentGuestCount' | 'maxCapacity' | 'registrationEnd'
>;

export const EventDetails = ({
  description,
  organizerName,
  currentGuestCount,
  maxCapacity,
  registrationEnd,
}: EventDetailProps) => {
  const { isUnlimited, progressValue, progressMax } = calculateCapacityStatus(
    maxCapacity,
    currentGuestCount
  );
  return (
    <Flex dir="column" gap="36px" margin="40px 0" padding="0 16px">
      <Flex dir="column" alignItems="flex-start" gap="12px">
        <Text as="h2" type="Heading" weight="semibold">
          이벤트 소개
        </Text>
        <Text>{description}</Text>
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
            신청 기간
          </Text>
          <Text>{formatKoreanDateTime(registrationEnd)}</Text>
        </Flex>
        <Flex
          dir="column"
          gap="12px"
          css={css`
            flex: 1;
          `}
        >
          <Text as="h2" type="Heading" weight="semibold">
            주최자
          </Text>
          <Text>{organizerName}</Text>
        </Flex>
      </Flex>
      <Flex width="100%" dir="column" alignItems="flex-start" gap="12px">
        <Text as="h2" type="Heading" weight="semibold">
          참여 현황
        </Text>
        <Flex width="100%" justifyContent="space-between" alignItems="center">
          <ProgressBar value={progressValue} max={progressMax} color={theme.colors.primary500} />
          <Flex width="20%" justifyContent="center" alignItems="flex-end">
            <Text type="Label" color={theme.colors.gray500} weight="semibold">
              {isUnlimited ? '무제한' : `${currentGuestCount} / ${maxCapacity}`}
            </Text>
          </Flex>
        </Flex>
      </Flex>
    </Flex>
  );
};
