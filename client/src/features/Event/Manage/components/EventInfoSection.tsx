import { css } from '@emotion/react';

import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';

type EventInfo = {
  eventId: number;
  title: string;
  description: string;
  place: string;
  organizerName: string;
  eventStart: string;
  eventEnd: string;
  registrationStart: string;
  registrationEnd: string;
  currentGuestCount: number;
  maxCapacity: number;
  questions: {
    questionId: number;
    questionText: string;
    isRequired: boolean;
    orderIndex: number;
  }[];
};

export const EventInfoSection = ({
  eventId,
  title,
  description,
  organizerName,
  place,
  eventStart,
  eventEnd,
  registrationStart,
  registrationEnd,
  currentGuestCount,
  maxCapacity,
  questions,
}: EventInfo) => {
  return (
    <Flex
      as="section"
      dir="column"
      gap="24px"
      width="100%"
      margin="10px"
      css={css`
        max-width: 800px;
        margin: 0 auto;
        padding: 0 16px;

        @media (max-width: 768px) {
          padding: 0 20px;
        }

        @media (max-width: 480px) {
          padding: 0 16px;
        }
      `}
    >
      <Card>
        <Flex dir="column" gap="16px">
          <Flex alignItems="center" gap="8px">
            <Icon name="calendar" size={14} color="#4A5565" />
            <Text type="caption" weight="regular" color="#4A5565">
              이벤트 정보
            </Text>
          </Flex>

          <Text type="Body" weight="semibold" color="#0A0A0A">
            {title}
          </Text>

          <Text type="caption" weight="regular" color="#4A5565">
            {description}
          </Text>

          <Flex alignItems="center" gap="8px">
            <Icon name="users" size={14} color="#4A5565" />
            <Text type="caption" weight="regular" color="#4A5565">
              {`주최자: ${organizerName}`}
            </Text>
          </Flex>

          <Flex alignItems="center" gap="8px">
            <Icon name="location" size={14} color="#4A5565" />
            <Text type="caption" weight="regular" color="#4A5565">
              {place}
            </Text>
          </Flex>

          <Flex alignItems="center" gap="8px">
            <Icon name="calendar" size={14} color="#4A5565" />
            <Text type="caption" weight="regular" color="#4A5565">
              {`신청 마감: ${registrationEnd}`}
            </Text>
          </Flex>

          <Flex alignItems="center" gap="8px">
            <Icon name="clock" size={14} color="#4A5565" />
            <Text type="caption" weight="regular" color="#4A5565">
              {`이벤트 일시: ${eventStart} ~ ${eventEnd}`}
            </Text>
          </Flex>
          <Spacing height="1px" color="#ECEEF2" />

          <Flex dir="column" gap="12px">
            <Flex justifyContent="space-between" alignItems="center">
              <Flex alignItems="center" gap="8px">
                <Icon name="users" size={14} color="#4A5565" />
                <Text type="caption" weight="regular" color="#4A5565">
                  참가 현황
                </Text>
              </Flex>
              <Text type="caption" weight="regular" color="#4A5565">
                {`${currentGuestCount}/${maxCapacity}명`}
              </Text>
            </Flex>
            <ProgressBar value={currentGuestCount} max={maxCapacity} color="#0A0A0A" />
          </Flex>
        </Flex>
      </Card>
    </Flex>
  );
};
