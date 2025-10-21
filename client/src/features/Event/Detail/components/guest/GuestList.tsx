import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { organizationQueryOptions } from '@/api/queries/organization';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';
import { theme } from '@/shared/styles/theme';

import type { Guest, NonGuest } from '../../../Manage/types';

import { PokeModal } from './PokeModal';

type GuestListProps = {
  eventId: number;
  title: string;
  titleColor: string;
  guests: (Guest | NonGuest)[];
  memberIdToGroup: Map<number, { groupId: number; name: string }>;
};

export const GuestList = ({
  eventId,
  title,
  titleColor,
  guests,
  memberIdToGroup,
}: GuestListProps) => {
  const { organizationId } = useParams();
  const { isOpen, open, close } = useModal();
  const [receiverGuest, setReceiverGuest] = useState<NonGuest | null>(null);
  const { data: joinedMember } = useQuery({
    ...organizationQueryOptions.joinedStatus(Number(organizationId)),
  });

  const handleGuestClick = (guest: NonGuest) => {
    if (!isAuthenticated() || !joinedMember?.isMember) return;
    setReceiverGuest(guest);
    open();
  };

  const grouped = (() => {
    if (memberIdToGroup.size === 0) return [];

    type GuestItem = Guest | NonGuest;
    const groupMap = new Map<string, { name: string; id: number; members: GuestItem[] }>();

    for (const guest of guests) {
      const info = memberIdToGroup.get(guest.organizationMemberId);
      if (!info) continue;
      const { groupId, name } = info;

      if (!groupMap.has(name)) groupMap.set(name, { name, id: groupId, members: [] });
      groupMap.get(name)!.members.push(guest);
    }

    const groupArr = Array.from(groupMap.values());
    groupArr.sort((g1, g2) => g1.id - g2.id);
    groupArr.forEach((group) =>
      group.members.sort((m1, m2) => (m1.nickname ?? '').localeCompare(m2.nickname ?? '', 'ko'))
    );

    return groupArr;
  })();

  return (
    <>
      <Flex dir="column" margin="0" padding="0 16px" gap="16px">
        <Flex alignItems="center" gap="8px">
          <Text type="Heading" weight="semibold" color={titleColor}>
            {title}
          </Text>
        </Flex>

        <Flex dir="column" gap="20px">
          {grouped.map((group) => (
            <section key={`${group.id}:${group.name}`}>
              <Text
                as="h3"
                type="Body"
                weight="semibold"
                color={theme.colors.gray700}
                css={css`
                  margin: 4px 0 10px;
                `}
              >
                {group.name}
              </Text>

              <Flex
                as="ul"
                dir="row"
                alignItems="flex-start"
                gap="8px"
                css={css`
                  flex-wrap: wrap;
                  list-style: none;
                `}
              >
                {group.members.map((guest) => (
                  <GuestBadge
                    key={guest.organizationMemberId}
                    onClick={() => handleGuestClick(guest)}
                  >
                    {guest.nickname}
                  </GuestBadge>
                ))}
              </Flex>
            </section>
          ))}
        </Flex>
      </Flex>
      {receiverGuest && (
        <PokeModal
          eventId={eventId}
          receiverGuest={receiverGuest}
          isOpen={isOpen}
          onClose={close}
        />
      )}
    </>
  );
};

const GuestBadge = styled.li`
  display: inline-block;
  align-items: center;
  width: fit-content;
  height: fit-content;
  background-color: ${theme.colors.gray100};
  color: ${theme.colors.gray600};
  padding: 4px 12px;
  border-radius: 8px;
  cursor: pointer;
  user-select: none;
  -webkit-user-select: none;

  &:hover {
    background-color: ${theme.colors.gray200};
  }
`;
