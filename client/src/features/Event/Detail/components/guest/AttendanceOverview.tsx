import { useSuspenseQueries, useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';
import { organizationQueryOptions } from '@/api/queries/organization';
import { Flex } from '@/shared/components/Flex';
import { theme } from '@/shared/styles/theme';

import { GuestList } from './GuestList';

export const AttendanceOverview = ({ eventId }: { eventId: number }) => {
  const { organizationId } = useParams();

  const [{ data: guests = [] }, { data: nonGuests = [] }] = useSuspenseQueries({
    queries: [eventQueryOptions.guests(eventId), eventQueryOptions.nonGuests(eventId)],
  });

  const { data: members = [] } = useQuery({
    ...organizationQueryOptions.members(Number(organizationId)),
    enabled: !!organizationId,
  });

  const memberIdToGroup = new Map<number, { groupId: number; name: string }>();
  members.forEach((m) => {
    if (m?.group?.groupId != null) {
      memberIdToGroup.set(m.organizationMemberId, {
        groupId: m.group.groupId,
        name: m.group.name,
      });
    }
  });

  return (
    <Flex as="section" width="100%" dir="column">
      <GuestList
        eventId={eventId}
        title={`신청 완료 (${guests.length}명)`}
        titleColor={theme.colors.primary600}
        guests={guests}
        memberIdToGroup={memberIdToGroup}
      />
      <GuestList
        eventId={eventId}
        title={`미신청 (${nonGuests.length}명)`}
        titleColor={theme.colors.red600}
        guests={nonGuests}
        memberIdToGroup={memberIdToGroup}
      />
    </Flex>
  );
};
