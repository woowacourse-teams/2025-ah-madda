import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';

import { guestManageQueryOptions } from '@/api/queries/guestManage';
import { Flex } from '@/shared/components/Flex';

import { Guest } from '../types';

import { AlarmSection } from './AlarmSection';
import { GuestViewSection } from './GuestViewSection';

type GuestManageSectionProps = {
  completedGuests: Guest[];
  pendingGuests: Guest[];
};

export const GuestManageSection = ({ completedGuests, pendingGuests }: GuestManageSectionProps) => {
  // const { eventId } = useParams();
  //E.TODO: eventId 가져오기
  const eventId = 1; //임시
  const { data: guests = [] } = useQuery({
    ...guestManageQueryOptions.guests(eventId),
  });

  const { data: nonGuests = [] } = useQuery({
    ...guestManageQueryOptions.nonGuests(eventId),
  });

  console.log(guests, nonGuests);

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
      <AlarmSection pendingGuestsCount={pendingGuests.length} />

      <GuestViewSection
        completedGuests={completedGuests}
        pendingGuests={pendingGuests}
        onGuestClick={() => {}}
      />
    </Flex>
  );
};
