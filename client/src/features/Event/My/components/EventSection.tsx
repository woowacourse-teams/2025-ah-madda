import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';

import { Event } from '../../types/Event';

import { EventCard } from './EventCard';

type EventSectionProps = {
  events: Event[];
  title: string;
  emptyMessage: string;
  cardType: 'host' | 'participate';
};

export const EventSection = ({ events, title, emptyMessage, cardType }: EventSectionProps) => {
  return (
    <Flex dir="column" gap="16px">
      <Flex alignItems="center" gap="8px">
        <Icon name="calendar" size={21} color="#0A0A0A" />
        <Text type="Body" weight="bold" color="black">
          {title}
        </Text>
      </Flex>

      {events.length > 0 ? (
        <EventCardContainer>
          {events.map((event) => (
            <EventCard key={event.eventId} {...event} cardType={cardType} />
          ))}
        </EventCardContainer>
      ) : (
        <Text type="Body" weight="regular" color="#666">
          {emptyMessage}
        </Text>
      )}
    </Flex>
  );
};

export const EventCardContainer = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;

  @media (min-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (min-width: 1024px) {
    grid-template-columns: repeat(3, 1fr);
  }
`;
