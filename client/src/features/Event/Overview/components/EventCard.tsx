import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';

import { Event } from '../../types/Event';
import { formatDateTime } from '../utils/formatDateTime';
import { formatTime } from '../utils/formatTime';

export const EventCard = ({
  eventId,
  title,
  description,
  registrationEnd,
  eventStart,
  eventEnd,
  place,
  organizerName,
  currentGuestCount,
  maxCapacity,
}: Event) => {
  const navigate = useNavigate();
  const isRegistrationOpen = new Date(registrationEnd) > new Date();

  return (
    <CardWrapper onClick={() => navigate(`/event/${eventId}`)}>
      <Flex dir="column" gap="8px">
        <Flex justifyContent="space-between" alignItems="center" gap="8px">
          <Text type="Title" color="#ffffff" weight="semibold">
            {title}
          </Text>
          <Badge isRegistrationOpen={isRegistrationOpen}>
            {isRegistrationOpen ? '모집중' : '모집마감'}
          </Badge>
        </Flex>
        <Text type="caption" color="#99A1AF">
          {description}
        </Text>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="calendar" color="#99A1AF" size={15} />
          <Text type="caption" color="#99A1AF">
            {`신청 마감 ${formatTime(registrationEnd)} 까지`}
          </Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="clock" color="#99A1AF" size={15} />
          <Text
            type="caption"
            color="#99A1AF"
          >{`이벤트 시간 ${formatDateTime(eventStart, eventEnd)}`}</Text>
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
            {`${currentGuestCount}/${maxCapacity} 명`}
          </Text>
        </Flex>
        <ProgressBar value={Number(currentGuestCount)} max={maxCapacity} color="black" />
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

const Badge = styled.span<{ isRegistrationOpen: boolean }>`
  background-color: ${({ isRegistrationOpen }) => (isRegistrationOpen ? '#2563EB' : 'gray')};
  color: #ffffff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
`;
