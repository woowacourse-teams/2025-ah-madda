import { useInfiniteQuery } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { EventCard } from '../../components/EventCard';
import { groupEventsByDate } from '../../utils/groupEventsByDate';
import { useInfiniteScroll } from '../hooks/useInfiniteScroll';

import { EventGrid } from './CurrentEventList';
import { EventSection } from './EventSection';

type PastEventListProps = {
  organizationId: number;
};

export const PastEventList = ({ organizationId }: PastEventListProps) => {
  const { data: pastEventData, fetchNextPage } = useInfiniteQuery({
    ...eventQueryOptions.past(organizationId),
    staleTime: 5 * 60 * 1000,
  });

  const { ref: observerRef } = useInfiniteScroll(fetchNextPage);
  const groupedEvents = groupEventsByDate(pastEventData?.pages.flat() ?? []);

  if (pastEventData?.pages.flat().length === 0) {
    return (
      <Flex justifyContent="center" alignItems="center" height="200px">
        <Text type="Heading" weight="semibold" color="gray">
          등록된 이벤트가 없습니다.
        </Text>
      </Flex>
    );
  }

  return (
    <>
      <Flex dir="column" width="100%" gap="20px" margin="0 0 20px 0">
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
      <div ref={observerRef} />
    </>
  );
};
