import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';

import type { EventInfo } from '../types';

type EventInfoSectionProps = {
  eventInfo: EventInfo;
};

export const EventInfoSection = ({ eventInfo }: EventInfoSectionProps) => {
  const {
    title,
    description,
    organizer,
    location,
    deadlineTime,
    startTime,
    endTime,
    currentParticipants,
    maxParticipants,
  } = eventInfo;

  return (
    <Flex as="section" dir="column" gap="24px" width="100%">
      <Card>
        <Flex dir="column" gap="16px" padding="24px">
          <Flex alignItems="center" gap="8px">
            <Icon name="calendar" size={17.5} color="#4A5565" />
            <Text type="Body" weight="regular" color="#4A5565">
              이벤트 정보
            </Text>
          </Flex>

          <Text type="Title" weight="semibold" color="#0A0A0A">
            {title}
          </Text>

          <Text type="Body" weight="regular" color="#4A5565">
            {description}
          </Text>

          <Flex alignItems="center" gap="8px">
            <Icon name="users" size={14} color="#4A5565" />
            <Text type="Body" weight="regular" color="#4A5565">
              {`주최자: ${organizer}`}
            </Text>
          </Flex>

          <Flex alignItems="center" gap="8px">
            <Icon name="location" size={14} color="#4A5565" />
            <Text type="Body" weight="regular" color="#4A5565">
              {location}
            </Text>
          </Flex>

          <Flex dir="column" gap="8px">
            <Flex alignItems="center" gap="8px">
              <Icon name="calendar" size={14} color="#4A5565" />
              <Text type="Body" weight="regular" color="#4A5565">
                {`신청 마감: ${deadlineTime}`}
              </Text>
            </Flex>

            <Flex alignItems="center" gap="8px">
              <Icon name="clock" size={14} color="#4A5565" />
              <Text type="Body" weight="regular" color="#4A5565">
                {`이벤트 일시: ${startTime} ~ ${endTime}`}
              </Text>
            </Flex>
          </Flex>

          <Flex dir="column" gap="12px">
            <Flex justifyContent="space-between" alignItems="center">
              <Flex alignItems="center" gap="8px">
                <Icon name="users" size={14} color="#4A5565" />
                <Text type="Body" weight="regular" color="#4A5565">
                  참가 현황
                </Text>
              </Flex>
              <Text type="caption" weight="regular" color="#4A5565">
                {`${currentParticipants}/${maxParticipants}명`}
              </Text>
            </Flex>
            <ProgressBar value={currentParticipants} max={maxParticipants} color="#0A0A0A" />
          </Flex>
        </Flex>
      </Card>
    </Flex>
  );
};
