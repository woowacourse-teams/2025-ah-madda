import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';
import { Flex } from '@/shared/components/Flex';

import { useCheckableGuests } from '../hooks/useCheckableGuests';

import { AlarmSection } from './AlarmSection';
import { GuestViewSection } from './GuestViewSection';

export const GuestManageSection = () => {
  const { eventId: eventIdParam } = useParams();
  const eventId = Number(eventIdParam);
  const { data: guests = [] } = useQuery(eventQueryOptions.guests(eventId));
  const { data: nonGuests = [] } = useQuery(eventQueryOptions.nonGuests(eventId));

  const {
    guestData,
    toggleAll: toggleGuestAllChecked,
    toggleItem: toggleGuestChecked,
    getCheckedGuests: getCheckedGuests,
  } = useCheckableGuests(guests);
  const {
    guestData: nonGuestData,
    toggleAll: toggleNonGuestAllChecked,
    toggleItem: toggleNonGuestChecked,
    getCheckedGuests: getCheckedNonGuests,
  } = useCheckableGuests(nonGuests);

  const checkedGuests = getCheckedGuests();
  const checkedNonGuests = getCheckedNonGuests();

  const selectedMemberIds = [
    ...checkedGuests.map((guest) => guest.organizationMemberId),
    ...checkedNonGuests.map((nonGuest) => nonGuest.organizationMemberId),
  ];

  const totalMessageReceiveGuestCount = selectedMemberIds.length;

  return (
    <Flex
      as="section"
      dir="column"
      gap="24px"
      width="100%"
      margin="0 auto"
      padding="0 16px"
      css={css`
        max-width: 800px;

        @media (max-width: 768px) {
          padding: 0 20px;
        }
      `}
    >
      <AlarmSection
        organizationMemberIds={selectedMemberIds}
        pendingGuestsCount={totalMessageReceiveGuestCount}
      />
      <GuestViewSection
        guests={guestData}
        onGuestChecked={toggleGuestChecked}
        onAllChecked={toggleGuestAllChecked}
        nonGuests={nonGuestData}
        onNonGuestChecked={toggleNonGuestChecked}
        onNonGuestAllChecked={toggleNonGuestAllChecked}
      />
    </Flex>
  );
};
