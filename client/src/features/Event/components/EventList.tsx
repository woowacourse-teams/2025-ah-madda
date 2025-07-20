import styled from '@emotion/styled';

import { EventContainer } from '../containers/EventContainer';
import { eventMock } from '../fixtures/mock';
import { groupEventsByDate } from '../utils/groupEventsByDate';

import { EventCard } from './EventCard';
import { EventSection } from './EventSection';

export const EventList = () => {
  const groupedEvents = groupEventsByDate(eventMock);

  return (
    <EventContainer>
      {Object.entries(groupedEvents).map(
        ([dateTitle, events]) =>
          events.length > 0 && (
            <EventSection key={dateTitle} title={dateTitle}>
              <EventGrid>
                {events.map((event, index) => (
                  <EventCard key={index} {...event} />
                ))}
              </EventGrid>
            </EventSection>
          )
      )}
    </EventContainer>
  );
};

export const EventGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem; /* gap-6 */

  @media (min-width: 768px) {
    grid-template-columns: repeat(2, 1fr); /* md:grid-cols-2 */
  }

  @media (min-width: 1024px) {
    grid-template-columns: repeat(3, 1fr); /* lg:grid-cols-3 */
  }
`;
