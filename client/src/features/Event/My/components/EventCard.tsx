import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';

import { Event } from '../../types/Event';

export const EventCard = ({
  title,
  description,
  organizerName,
  registrationEnd,
  eventStart,
  eventEnd,
  place,
  currentGuestCount,
  maxCapacity,
}: Event) => {
  const navigate = useNavigate();

  return (
    <EventCardWrapper onClick={() => navigate('/event/manage')}>
      <Flex dir="column" gap="3.5px">
        <Text type="Title" weight="semibold" color="white">
          {title}
        </Text>
        <Text type="caption" weight="regular" color="#B0B0B0">
          {description}
        </Text>
      </Flex>

      <Flex dir="column" gap="10px">
        <Flex alignItems="baseline" gap="3.5px">
          <Icon name="calendar" size={14} color="#A0A0A0" />
          <Text type="caption" weight="regular" color="#A0A0A0">
            {`신청 마감 ${registrationEnd}`}
          </Text>
        </Flex>

        <Flex alignItems="baseline" gap="3.5px">
          <Icon name="clock" size={14} color="#A0A0A0" />
          <Text type="caption" weight="regular" color="#A0A0A0">
            {`이벤트 시간 ${eventStart} - ${eventEnd}`}
          </Text>
        </Flex>

        <Flex gap="7px" alignItems="center">
          <Icon name="location" size={10.5} color="#A0A0A0" />
          <Text type="caption" weight="regular" color="#A0A0A0">
            {`장소 ${place}`}
          </Text>
        </Flex>
      </Flex>

      <Flex dir="column" gap="14px" alignItems="flex-end">
        <Text type="caption" weight="regular" color="white">
          {organizerName}
        </Text>
        <ProgressBar value={currentGuestCount} max={maxCapacity} color="black" />
      </Flex>

      <Flex>
        <Text type="caption" weight="regular" color="#A0A0A0">
          {`${currentGuestCount}/${maxCapacity}명 참여`}
        </Text>
      </Flex>
    </EventCardWrapper>
  );
};

const EventCardWrapper = styled.section`
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 14px;
  width: 100%;
  min-width: 300px;
  background-color: #1e2939;
  border-radius: 8.75px;
  cursor: pointer;

  &:hover {
    background-color: #2e3b4d;
  }
`;
