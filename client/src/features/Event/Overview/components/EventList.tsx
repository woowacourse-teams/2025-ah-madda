import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { Event } from '../../types/Event';
import { EventContainer } from '../containers/EventContainer';
import { groupEventsByDate } from '../utils/groupEventsByDate';

import { ActionButtons } from './ActionButtons';
import { EventCard } from './EventCard';
import { EventSection } from './EventSection';

type EventListProps = {
  events: Event[];
};

export const EventList = ({ events }: EventListProps) => {
  const groupedEvents = groupEventsByDate(events);
  const navigate = useNavigate();

  return (
    <EventContainer>
      <Flex
        width="100%"
        justifyContent="flex-start"
        alignItems="center"
        gap="5px"
        padding="10px"
        css={css`
          border-radius: 8px;
          background-color: ${theme.colors.primary50};
        `}
      >
        <Icon name="calendar" color="primary500" size={20} />
        <Text weight="bold" color={theme.colors.primary500}>
          {events.length}개의 이벤트가 열려있어요!
        </Text>
      </Flex>
      <Flex dir="column" width="100%" gap="20px">
        <ActionButtons />
        {groupedEvents.length === 0 ? (
          <Flex justifyContent="center" alignItems="center" height="200px">
            <Text type="Heading" weight="semibold">
              등록된 이벤트가 없습니다.
            </Text>
          </Flex>
        ) : (
          groupedEvents.map(({ label, events }) => (
            <EventSection key={label} title={label}>
              <EventGrid>
                {events.map((event, index) => (
                  <EventCard key={index} {...event} />
                ))}
              </EventGrid>
            </EventSection>
          ))
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
