import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';

import { Event } from '../../types/Event';

export const EventCard = ({
  title,
  description,
  registrationEnd,
  eventStart,
  eventEnd,
  place,
  organizerName,
}: Omit<Event, 'id'>) => {
  return (
    <CardWrapper>
      <Flex dir="column" gap="8px">
        <Text type="Title" color="#ffffff" weight="semibold">
          {title}
        </Text>
        <Text type="caption" color="#99A1AF">
          {description}
        </Text>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="calendar" color="#99A1AF" size={15} />
          <Text type="caption" color="#99A1AF">
            {`신청 마감 ${registrationEnd}`}
          </Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="clock" color="#99A1AF" size={15} />
          <Text type="caption" color="#99A1AF">{`이벤트 시간 ${eventStart} - ${eventEnd}`}</Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="location" color="#99A1AF" size={15} />
          <Text type="caption" color="#99A1AF">
            {`장소 ${place}`}
          </Text>
        </Flex>
        <Spacing />
        <Flex width="100%" justifyContent="space-between" alignItems="center">
          <Text type="caption" color="#99A1AF">
            주최자
          </Text>
          <Text type="caption" color="#99A1AF">
            {organizerName}
          </Text>
        </Flex>
        <Flex width="100%" justifyContent="space-between" alignItems="center">
          <Text type="caption" color="#99A1AF">
            참여 현황
          </Text>
          <Text type="caption" color="#99A1AF">
            42/50명
          </Text>
        </Flex>
        <ProgressBar value={45} max={50} color="black" />
      </Flex>
    </CardWrapper>
  );
};

const CardWrapper = styled.div`
  cursor: pointer;
  background-color: #232838;
  box-shadow: rgba(0, 0, 0, 0.3) 0px 4px 12px;
  padding: 22px;
  border-radius: 12px;
  width: 100%;

  &:hover {
    background-color: rgba(48, 65, 81, 0.95);
  }
`;

const Spacing = styled.hr`
  width: 100%;
  height: 1px;
  background-color: rgb(218, 218, 218);
  border: none;
  margin: 0;
`;
