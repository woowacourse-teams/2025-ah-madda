import { css } from '@emotion/react';
import { useSuspenseQueries } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { GuestViewSection } from '@/features/Event/Manage/components/GuestViewSection';
import { Flex } from '@/shared/components/Flex';

export const ApplicationsTab = ({ eventId }: { eventId: number }) => {
  const [{ data: guests = [] }, { data: nonGuests = [] }] = useSuspenseQueries({
    queries: [eventQueryOptions.guests(eventId), eventQueryOptions.nonGuests(eventId)],
  });

  return (
    <Flex
      css={css`
        max-width: 800px;
        margin: 0 auto;
        padding: 20px 16px;

        @media (max-width: 768px) {
          padding: 20px;
        }

        @media (max-width: 480px) {
          padding: 16px;
        }
      `}
    >
      <GuestViewSection guests={guests} nonGuests={nonGuests} />
    </Flex>
  );
};
