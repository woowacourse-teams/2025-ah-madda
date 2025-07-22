import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';

import { Event } from '../../types/Event';
import { EventContainer } from '../containers/EventContainer';
import { groupEventsByDate } from '../utils/groupEventsByDate';

import { EventCard } from './EventCard';
import { EventSection } from './EventSection';

type Props = {
  events: Event[];
};

export const EventList = ({ events }: Props) => {
  const groupedEvents = groupEventsByDate(events);
  const navigate = useNavigate();

  return (
    <EventContainer>
      <Flex
        dir="column"
        width="100%"
        gap="20px"
        css={css`
          max-width: 1120px;
          margin: 0 auto;
        `}
      >
        <Flex justifyContent="flex-end" alignItems="center">
          <Button width="130px" size="md" onClick={() => navigate('/event/new')}>
            + 이벤트 생성
          </Button>
        </Flex>
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
      </Flex>
    </EventContainer>
  );
};

export const EventGrid = styled.div`
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
