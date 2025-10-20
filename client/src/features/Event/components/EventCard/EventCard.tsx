import { memo } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate, useParams } from 'react-router-dom';

import { Badge } from '@/shared/components/Badge';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { trackClickEventCard } from '@/shared/lib/gaEvents';
import { theme } from '@/shared/styles/theme';
import { formatDate } from '@/shared/utils/dateUtils';

import { Event } from '../../types/Event';
import { badgeText } from '../../utils/badgeText';
import { calculateCapacityStatus } from '../../utils/calculateCapacityStatus';
import { normalizeWhitespace } from '../../utils/normalizeWhitespace';

export type EventCardType = 'default' | 'host' | 'participate';

export type EventCardProps = Event & {
  cardType?: EventCardType;
};

export const EventCard = memo(function EventCard({
  eventId,
  title,
  description,
  registrationEnd,
  eventStart,
  eventEnd,
  place,
  organizerNicknames,
  currentGuestCount,
  maxCapacity,
  isGuest,
  cardType = 'default',
}: EventCardProps) {
  const navigate = useNavigate();
  const { organizationId } = useParams();
  const { isUnlimited, progressValue, progressMax } = calculateCapacityStatus(
    maxCapacity,
    currentGuestCount
  );

  const handleClickCard = () => {
    trackClickEventCard(title);

    if (cardType === 'host') {
      navigate(`/${organizationId}/event/manage/${eventId}`);
    } else {
      navigate(`/${organizationId}/event/${eventId}`);
    }
  };

  return (
    <CardWrapper onClick={handleClickCard}>
      <Flex dir="column" justifyContent={description ? 'flex-start' : 'space-between'} gap="8px">
        <Flex justifyContent="space-between" alignItems="center" width="100%">
          <Badge variant={badgeText(registrationEnd).color}>
            {badgeText(registrationEnd).text}
          </Badge>
          {isGuest && <Badge variant="yellow">참여</Badge>}
        </Flex>
        <Flex justifyContent="space-between" alignItems="center" gap="8px">
          <Text as="h2" type="Heading" color={theme.colors.gray900} weight="semibold">
            {title.length > 17 ? `${title.slice(0, 19)}...` : title}
          </Text>
        </Flex>
        {/* S.TODO: 추후 구조 개선 */}
        <Flex
          height="20px"
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
            {normalizeWhitespace(description)}
          </Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="location" size={16} color="gray500" />
          <Text type="Label" color="#99A1AF">
            {place}
          </Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="clock" size={16} color="gray500" />
          <Text type="Label" color={theme.colors.gray500}>
            {formatDate({
              start: eventStart,
              end: eventEnd,
              pattern: 'MM.DD E HH:mm',
              options: {
                dayOfWeek: 'shortParen',
                smartRange: true,
              },
            })}
          </Text>
        </Flex>
        <Flex alignItems="center" gap="4px" height="100%">
          <Icon name="user" size={16} color="gray500" />
          <Text type="Label" color={theme.colors.gray500}>
            {organizerNicknames.length > 1
              ? `${organizerNicknames[0]} 외 ${organizerNicknames.length - 1}명`
              : organizerNicknames[0]}{' '}
            주최
          </Text>
        </Flex>
        <Spacing height="2px" />
        <Flex width="100%" justifyContent="space-between" alignItems="center" gap="8px">
          <ProgressBar value={progressValue} max={progressMax} color={theme.colors.primary500} />
          <Flex width="15%" justifyContent="center" alignItems="center">
            <Text type="Label" color="#99A1AF" weight="semibold">
              {isUnlimited ? '무제한' : `${currentGuestCount} / ${maxCapacity}`}
            </Text>
          </Flex>
        </Flex>
      </Flex>
    </CardWrapper>
  );
});

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
