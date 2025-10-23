import { useSuspenseQueries, useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';
import { organizationQueryOptions } from '@/api/queries/organization';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Spacing } from '@/shared/components/Spacing';
import { Tooltip } from '@/shared/components/Tooltip/Tooltip';
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
    <Flex as="section" width="100%" dir="column" gap="8px" padding="0 0 40px 0">
      <Flex justifyContent="flex-end" padding="12px 0 0 0">
        <Tooltip
          placement="left-bottom"
          content={`포키 알림은 로그인 후 스페이스에 참여한 친구에게만 보낼 수 있어요.\n친구를 클릭해 알림을 보내보세요!`}
        >
          <Icon name="info" color="gray500" size={28} />
        </Tooltip>
      </Flex>
      <GuestList
        eventId={eventId}
        title={`신청 완료 (${guests.length}명)`}
        titleColor={theme.colors.primary600}
        guests={guests}
        memberIdToGroup={memberIdToGroup}
      />
      <Spacing height="16px" />
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
