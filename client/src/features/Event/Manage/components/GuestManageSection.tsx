import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';

import { guestManageQueryOptions } from '@/api/queries/guestManage';
import { Flex } from '@/shared/components/Flex';

import { AlarmSection } from './AlarmSection';
import { GuestViewSection } from './GuestViewSection';

export const GuestManageSection = () => {
  // const { eventId } = useParams();
  //E.TODO: eventId 가져오기
  const eventId = 1; //임시
  const { data: guests = [] } = useQuery({
    ...guestManageQueryOptions.guests(eventId),
  });

  const { data: nonGuests = [] } = useQuery({
    ...guestManageQueryOptions.nonGuests(eventId),
  });

  return (
    <Flex
      as="section"
      dir="column"
      gap="24px"
      width="100%"
      margin="10px"
      css={css`
        max-width: 800px;
        margin: 0 auto;
        padding: 0 16px;

        @media (max-width: 768px) {
          padding: 0 20px;
        }

        @media (max-width: 480px) {
          padding: 0 16px;
        }
      `}
    >
      <AlarmSection pendingGuestsCount={nonGuests.length} />

      <GuestViewSection guests={guests} nonGuests={nonGuests} />
    </Flex>
  );
};
