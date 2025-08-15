import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { UNLIMITED_CAPACITY } from '../../New/constants/errorMessages';
import { formatDateTime } from '../../Overview/utils/formatDateTime';
import { formatTime } from '../../Overview/utils/formatTime';
import { Event } from '../../types/Event';
import { TAB_VALUES } from '../constants';

type EventCardProps = Event & {
  cardType: (typeof TAB_VALUES)[keyof typeof TAB_VALUES];
};

export const EventCard = ({
  eventId,
  title,
  description,
  organizerName,
  registrationEnd,
  eventStart,
  eventEnd,
  place,
  currentGuestCount,
  maxCapacity,
  cardType,
}: EventCardProps) => {
  const navigate = useNavigate();

  const isUnlimited = maxCapacity === UNLIMITED_CAPACITY;
  const progressValue = isUnlimited ? 1 : Number(currentGuestCount);
  const progressMax = isUnlimited ? 1 : maxCapacity;
  const progressColor = isUnlimited ? theme.colors.primary700 : 'black';

  const handleClick = () => {
    if (cardType === 'host') {
      navigate(`/event/manage/${eventId}`);
    } else {
      navigate(`/event/${eventId}`);
    }
  };

  return (
    <EventCardWrapper onClick={handleClick}>
      <Flex dir="column" gap="3.5px">
        <Text as="h2" type="Heading" weight="semibold" color="white">
          {title.length > 15 ? `${title.slice(0, 12)}...` : title}
        </Text>
        <Text type="Body" weight="regular" color="#99A1AF">
          {description}
        </Text>
      </Flex>

      <Flex dir="column" gap="10px">
        <Flex alignItems="center" gap="3.5px">
          <Icon name="calendar" size={16} color="white" />
          <Text type="Label" weight="regular" color="#99A1AF">
            {`신청 마감 ${formatTime(registrationEnd)} 까지`}
          </Text>
        </Flex>

        <Flex alignItems="center" gap="3.5px">
          <Icon name="clock" size={16} color="white" />
          <Text type="Label" weight="regular" color="#99A1AF">
            {`이벤트 시간 ${formatDateTime(eventStart, eventEnd)}`}
          </Text>
        </Flex>

        <Flex gap="7px" alignItems="center">
          <Icon name="location" size={16} color="white" />
          <Text type="Label" weight="regular" color="#99A1AF">
            {`장소 ${place}`}
          </Text>
        </Flex>
      </Flex>
      <Spacing height="1px" color=" rgb(218, 218, 218);" />

      <Flex dir="column" gap="14px" alignItems="flex-end">
        <Flex width="100%" justifyContent="space-between" alignItems="center">
          <Text type="Label" color="#99A1AF">
            주최자
          </Text>
          <Text type="Label" weight="regular" color="#99A1AF">
            {organizerName}
          </Text>
        </Flex>

        <Flex width="100%" justifyContent="space-between" alignItems="center">
          <Text type="Label" color="#99A1AF">
            참여 현황
          </Text>
          <Text type="Label" color="#99A1AF">
            {isUnlimited ? '제한없음' : `${currentGuestCount}/${maxCapacity} 명`}
          </Text>
        </Flex>
        <ProgressBar value={progressValue} max={progressMax} color={progressColor} />
      </Flex>
    </EventCardWrapper>
  );
};

const EventCardWrapper = styled.section`
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 22px;
  width: 100%;
  min-width: 300px;
  background-color: #232838;
  border-radius: 12px;
  cursor: pointer;

  &:hover {
    background-color: rgba(48, 65, 81, 0.95);
  }
`;
