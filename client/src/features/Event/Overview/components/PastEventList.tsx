import { Flex } from '@/shared/components/Flex';

import { EventCard } from '../../components/EventCard';
import { Event } from '../../types/Event';
import { groupEventsByDate } from '../../utils/groupEventsByDate';

import { EventGrid } from './CurrentEventList';
import { EventSection } from './EventSection';

type PastEventListProps = {
  events: Event[];
};

export const PastEventList = ({ events }: PastEventListProps) => {
  const groupedEvents = groupEventsByDate(events);

  return (
    <Flex dir="column" width="100%" gap="20px">
      {groupedEvents
        .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
        .map(({ label, events }) => (
          <EventSection key={label} title={label}>
            <EventGrid>
              {events.map((event, index) => (
                <EventCard key={index} {...event} cardType="default" />
              ))}
            </EventGrid>
          </EventSection>
        ))}
    </Flex>
  );
};
