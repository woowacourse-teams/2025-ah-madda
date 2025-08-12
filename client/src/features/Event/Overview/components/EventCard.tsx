import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { Badge } from '@/shared/components/Badge';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { trackClickEventCard } from '@/shared/lib/gaEvents';
import { theme } from '@/shared/styles/theme';

import { Event } from '../../types/Event';
import { badgeText } from '../utils/badgeText';
import { calculateCapacityStatus } from '../utils/calculateCapacityStatus';
import { formatDateTime } from '../utils/formatDateTime';
import { removeNewline } from '../utils/removeNewline';

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

  const { isUnlimited, progressValue, progressMax } = calculateCapacityStatus(
    maxCapacity,
    currentGuestCount
  );

  const handleClickCard = () => {
    trackClickEventCard(title);
    navigate(`/event/${eventId}`);
  };

  return (
    <CardWrapper onClick={handleClickCard}>
      <Flex dir="column" gap="8px">
        <Badge variant={badgeText(registrationEnd).color}>{badgeText(registrationEnd).text}</Badge>
        <Flex justifyContent="space-between" alignItems="center" gap="8px">
          <Text as="h2" type="Heading" color={theme.colors.gray900} weight="semibold">
            {title.length > 17 ? `${title.slice(0, 19)}...` : title}
          </Text>
        </Flex>
        {/* S.TODO: 추후 구조 개선 */}
        <Flex
          css={css`
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            line-height: 1.4;
            word-break: break-word;
          `}
        >
          <Text type="Body" color={theme.colors.gray700}>
            {removeNewline(description)}
          </Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="location" size={16} color="gray" />
          <Text type="Label" color="#99A1AF">
            {place}
          </Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="clock" size={16} color="gray" />
          <Text type="Label" color={theme.colors.gray500}>
            {formatDateTime(eventStart, eventEnd)}
          </Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="user" size={16} color="gray" />
          <Text type="Label" color={theme.colors.gray500}>
            {organizerName} 주최
          </Text>
        </Flex>
        <Spacing height="2px" />
        {/* {formatTime(registrationEnd)} */}
        <Flex justifyContent="space-between" alignItems="center" gap="20px">
          <ProgressBar value={progressValue} max={progressMax} color={theme.colors.primary500} />
          <Text type="Label" color="#99A1AF" weight="semibold">
            {isUnlimited ? '무제한' : `${currentGuestCount}/${maxCapacity}`}
          </Text>
        </Flex>
      </Flex>
    </CardWrapper>
  );
};

const CardWrapper = styled.div`
  cursor: pointer;
  background-color: ${theme.colors.white};
  box-shadow: rgba(0, 0, 0, 0.1) 0px 4px 12px;
  padding: 22px;
  border-radius: 12px;
  width: 100%;

  &:hover {
    background-color: ${theme.colors.gray50};
  }
`;
