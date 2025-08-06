import { useSuspenseQueries } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { Flex } from '@/shared/components/Flex';

import { GuestDetailSection } from './GuestDetailSection';

export const ApplicationsTab = ({ eventId }: { eventId: number }) => {
  const [{ data: guests = [] }, { data: nonGuests = [] }] = useSuspenseQueries({
    queries: [eventQueryOptions.guests(eventId), eventQueryOptions.nonGuests(eventId)],
  });

  return (
    <Flex margin="20px 0 40px 0">
      <GuestDetailSection guests={guests} nonGuests={nonGuests} />
    </Flex>
  );
};
