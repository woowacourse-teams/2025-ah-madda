import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';

import { Event } from '../types';

import { EventCard } from './EventCard';

type EventSectionProps = {
  events: Event[];
  title: string;
  emptyMessage: string;
};

export const EventSection = ({ events, title, emptyMessage }: EventSectionProps) => {
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
            <EventCard key={event.id} {...event} />
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

const EventCardContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
`;
