import { useSuspenseQueries } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';
import { Flex } from '@/shared/components/Flex';

import { useCheckableGuests } from '../hooks/useCheckableGuests';

import { AlarmSection } from './AlarmSection';
import { GuestViewSection } from './GuestViewSection';
import { Statistics } from './Statistics';

export const GuestManageSection = () => {
  const { eventId: eventIdParam } = useParams();
  const eventId = Number(eventIdParam);

  const [{ data: guests = [] }, { data: nonGuests = [] }, { data: statisticsData = [] }] =
    useSuspenseQueries({
      queries: [
        eventQueryOptions.guests(eventId),
        eventQueryOptions.nonGuests(eventId),
        eventQueryOptions.statistic(eventId),
      ],
    });

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

  const selectedGuestCount = selectedMemberIds.length;

  return (
    <Flex as="section" dir="column" gap="24px" width="100%" margin="0 auto" padding="20px 0">
      <Statistics statistics={statisticsData} />
      <AlarmSection
        organizationMemberIds={selectedMemberIds}
        selectedGuestCount={selectedGuestCount}
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
