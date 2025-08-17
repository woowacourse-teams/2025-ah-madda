import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { EventCard } from '../../components/EventCard';
import { Event } from '../../types/Event';

type EventSectionProps = {
  events: Event[];
  date: string;
  cardType: 'host' | 'participate';
};

export const EventSection = ({ events, date, cardType }: EventSectionProps) => {
  return (
    <Flex as="section" dir="column" gap="20px" width="100%" margin="0 0 20px 0">
      <Flex alignItems="center" gap="4px">
        <Text as="h2" type="Heading" weight="bold">
          {date}
        </Text>
      </Flex>

      <EventCardContainer>
        {events.map((event) => (
          <EventCard key={event.eventId} {...event} cardType={cardType} />
        ))}
      </EventCardContainer>
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
