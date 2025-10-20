import { css } from '@emotion/react';
import { useSuspenseQueries } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Spacing } from '@/shared/components/Spacing';

import { useCheckableGuests } from '../../hooks/useCheckableGuests';
import { AlarmComposer } from '../alarm/ReminderForm';
import { AlarmHeader } from '../alarm/ReminderHeader';

import { GuestViewSection } from './GuestViewSection';

export const GuestManageSection = () => {
  const { eventId: eventIdParam } = useParams();
  const eventId = Number(eventIdParam);

  const [{ data: guests = [] }, { data: nonGuests = [] }, { data: notifyData = [] }] =
    useSuspenseQueries({
      queries: [
        eventQueryOptions.guests(eventId),
        eventQueryOptions.nonGuests(eventId),
        eventQueryOptions.history(eventId),
      ],
    });

  const {
    guestData,
    toggleAll: toggleGuestAllChecked,
    toggleItem: toggleGuestChecked,
    getCheckedGuests,
  } = useCheckableGuests(guests);

  const {
    guestData: nonGuestData,
    toggleAll: toggleNonGuestAllChecked,
    toggleItem: toggleNonGuestChecked,
    getCheckedGuests: getCheckedNonGuests,
  } = useCheckableGuests(nonGuests);

  const selectedMemberIds = [
    ...getCheckedGuests().map((g) => g.organizationMemberId),
    ...getCheckedNonGuests().map((ng) => ng.organizationMemberId),
  ];
  const selectedGuestCount = selectedMemberIds.length;

  return (
    <Flex as="section" dir="column" gap="24px" width="100%" margin="0 auto" padding="38px 0">
      <Card
        css={css`
          border: none;
        `}
      >
        <Flex as="section" dir="column">
          <AlarmHeader selectedGuestCount={selectedGuestCount} notifyData={notifyData} />
          <Spacing height="24px" />
          <GuestViewSection
            guests={guestData}
            onGuestChecked={toggleGuestChecked}
            onAllChecked={toggleGuestAllChecked}
            nonGuests={nonGuestData}
            onNonGuestChecked={toggleNonGuestChecked}
            onNonGuestAllChecked={toggleNonGuestAllChecked}
          />
          <Spacing height="28px" />
          <AlarmComposer
            organizationMemberIds={selectedMemberIds}
            selectedGuestCount={selectedGuestCount}
          />
        </Flex>
      </Card>
    </Flex>
  );
};
