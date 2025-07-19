import { Flex } from '../../../shared/components/Flex';
import { Icon } from '../../../shared/components/Icon';
import { ProgressBar } from '../../../shared/components/ProgressBar';
import { Text } from '../../../shared/components/Text';
import type { Event } from '../types';

type EventCardProps = {
  event: Event;
};

export const EventCard = ({ event }: EventCardProps) => {
  const {
    title,
    description,
    author,
    deadlineTime,
    startTime,
    endTime,
    location,
    currentParticipants,
    maxParticipants,
  } = event;

  return (
    <Flex
      dir="column"
      gap="12px"
      css={{
        padding: '14px',
        backgroundColor: '#1E2939',
        borderRadius: '8.75px',
        maxWidth: '350px',
      }}
    >
      <Flex justifyContent="space-between" alignItems="flex-start">
        <Flex dir="column">
          <Text type="caption" weight="regular" color="#A0A0A0">
            {`마감 · ${deadlineTime}`}
          </Text>
          <Text type="caption" weight="regular" color="#A0A0A0">
            {`시작 · ${startTime} · 종료 · ${endTime}`}
          </Text>
        </Flex>
      </Flex>

      <Flex dir="column" gap="3.5px">
        <Text type="Title" weight="semibold" color="white">
          {title}
        </Text>
        <Text type="caption" weight="regular" color="#B0B0B0">
          {description}
        </Text>
      </Flex>

      <Flex gap="7px" alignItems="center">
        <Icon name="location" size={10.5} color="#A0A0A0" />
        <Text type="caption" weight="regular" color="#A0A0A0">
          {location}
        </Text>
      </Flex>

      <Flex dir="column" gap="14px" alignItems="flex-end">
        <Text type="caption" weight="regular" color="white">
          {author}
        </Text>
        <ProgressBar value={currentParticipants} max={maxParticipants} />
      </Flex>

      <Flex>
        <Text type="caption" weight="regular" color="#A0A0A0">
          {`${currentParticipants}/${maxParticipants}명 참여`}
        </Text>
      </Flex>
    </Flex>
  );
};
