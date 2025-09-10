import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';
import { theme } from '@/shared/styles/theme';

import { EventCard } from '../../components/EventCard';
import { Event } from '../../types/Event';

import { EventGrid } from './CurrentEventList';
import { EventSection } from './EventSection';

type PastEventListProps = {
  events: Event[];
};

export const PastEventList = ({ events }: PastEventListProps) => {
  return (
    <>
      <Flex
        width="100%"
        justifyContent="flex-start"
        alignItems="center"
        gap="5px"
        margin="10px 0 "
        css={css`
          border-radius: 8px;
          background-color: ${theme.colors.primary50};
        `}
      >
        {events.map((event) => (
          <EventSection key={event.eventId} title={event.title}>
            <EventGrid>
              {events.map((event, index) => (
                <EventCard key={index} {...event} cardType="default" />
              ))}
            </EventGrid>
          </EventSection>
        ))}
      </Flex>
    </>
  );
};
