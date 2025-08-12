import { useSuspenseQueries } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { Flex } from '@/shared/components/Flex';
import { theme } from '@/shared/styles/theme';

import { GuestList } from './GuestList';

export const AttendanceOverview = ({ eventId }: { eventId: number }) => {
  const [{ data: guests = [] }, { data: nonGuests = [] }] = useSuspenseQueries({
    queries: [eventQueryOptions.guests(eventId), eventQueryOptions.nonGuests(eventId)],
  });

  return (
    <>
      <Flex as="section" width="100%" dir="column">
        <GuestList
          title={`신청 완료 (${guests.length}명)`}
          titleColor={theme.colors.primary600}
          guests={guests}
        />
        <GuestList
          title={`미신청 (${nonGuests.length}명)`}
          titleColor={theme.colors.red600}
          guests={nonGuests}
        />
      </Flex>
    </>
  );
};
