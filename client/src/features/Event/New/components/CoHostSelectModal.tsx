import { useEffect, useMemo, useState } from 'react';

import styled from '@emotion/styled';

import type { OrganizationMember } from '@/api/types/organizations';
import { GuestList } from '@/features/Event/Manage/components/GuestList';
import type { NonGuest } from '@/features/Event/Manage/types';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { theme } from '@/shared/styles/theme';

export type CoHostSelectModalProps = {
  isOpen: boolean;
  members: OrganizationMember[];
  initialSelectedIds: number[];
  onClose: () => void;
  onSubmit: (ids: number[]) => void;
  maxSelectable?: number;
  title?: string;
};

type GroupKey = 'ALL' | number;
type GroupSegment = { id: GroupKey; name: string };

export const CoHostSelectModal = ({
  isOpen,
  members,
  initialSelectedIds,
  onClose,
  onSubmit,
  maxSelectable,
  title = '공동 주최자 지정',
}: CoHostSelectModalProps) => {
  const { error } = useToast();
  const [cohostList, setCohostList] = useState<NonGuest[]>([]);
  const [activeGroup, setActiveGroup] = useState<GroupKey>('ALL');

  useEffect(() => {
    const init = (members || []).map<NonGuest>((m) => ({
      organizationMemberId: m.organizationMemberId,
      nickname: m.nickname,
      imageUrl: undefined,
      isChecked: initialSelectedIds.includes(m.organizationMemberId),
    }));
    setCohostList(init);
  }, [isOpen, members, initialSelectedIds]);

  const memberIdToGroupId = useMemo(() => {
    const map = new Map<number, number>();
    (members || []).forEach((m) => {
      map.set(m.organizationMemberId, m.group.groupId);
    });
    return map;
  }, [members]);

  const groupSegments = useMemo<GroupSegment[]>(() => {
    const groups = new Map<number, string>();
    (members || []).forEach((m) => {
      const { groupId, name } = m.group;
      if (!groups.has(groupId)) groups.set(groupId, name);
    });

    const entries: Array<[number, string]> = Array.from(groups.entries()).sort(([a], [b]) => a - b);

    return [{ id: 'ALL', name: '전체' }, ...entries.map(([id, name]) => ({ id, name }))];
  }, [members]);

  const visibleIds = useMemo(() => {
    if (activeGroup === 'ALL') return cohostList.map((m) => m.organizationMemberId);
    return cohostList
      .map((m) => m.organizationMemberId)
      .filter((id) => memberIdToGroupId.get(id) === activeGroup);
  }, [cohostList, activeGroup, memberIdToGroupId]);

  const filteredGuests = useMemo<NonGuest[]>(
    () =>
      cohostList.filter((m) =>
        activeGroup === 'ALL' ? true : memberIdToGroupId.get(m.organizationMemberId) === activeGroup
      ),
    [cohostList, activeGroup, memberIdToGroupId]
  );

  const selectedTotalCount = cohostList.filter((m) => m.isChecked).length;

  const onGuestChecked = (organizationMemberId: number) => {
    setCohostList((prev) => {
      const next = prev.map((m) =>
        m.organizationMemberId === organizationMemberId ? { ...m, isChecked: !m.isChecked } : m
      );

      if (maxSelectable != null && maxSelectable > 0) {
        const count = next.filter((m) => m.isChecked).length;
        if (count > maxSelectable) {
          error(`최대 ${maxSelectable}명까지 선택할 수 있어요.`);
          return prev;
        }
      }

      return next;
    });
  };

  const onAllGuestChecked = () => {
    const allVisibleChecked = filteredGuests.length > 0 && filteredGuests.every((m) => m.isChecked);

    setCohostList((prev) => {
      const toggled = prev.map((m) =>
        visibleIds.includes(m.organizationMemberId) ? { ...m, isChecked: !allVisibleChecked } : m
      );

      if (maxSelectable != null && maxSelectable > 0) {
        const count = toggled.filter((m) => m.isChecked).length;
        if (count > maxSelectable) {
          error(`최대 ${maxSelectable}명까지 선택할 수 있어요.`);
          return prev;
        }
      }
      return toggled;
    });
  };

  const handleApply = () => {
    const ids = cohostList.filter((m) => m.isChecked).map((m) => m.organizationMemberId);
    onSubmit(ids);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalBody dir="column" gap="12px">
        <Text as="label" type="Heading" weight="medium">
          {title}
        </Text>

        <Flex css={{ flexWrap: 'wrap' }} gap="8px">
          {groupSegments.map((g) => {
            const isSelected = activeGroup === g.id;
            return (
              <Segment
                key={`${g.id}`}
                type="button"
                isSelected={isSelected}
                onClick={() => setActiveGroup(g.id)}
                aria-pressed={isSelected}
              >
                <Text
                  weight={isSelected ? 'bold' : 'regular'}
                  color={isSelected ? theme.colors.primary500 : theme.colors.gray300}
                >
                  {g.name}
                </Text>
              </Segment>
            );
          })}
        </Flex>

        <ScrollArea>
          <GuestList
            title={`${title} (${selectedTotalCount}명)`}
            titleColor={theme.colors.gray700}
            guests={filteredGuests}
            onGuestChecked={onGuestChecked}
            onAllGuestChecked={onAllGuestChecked}
          />
        </ScrollArea>

        <Text type="Label" color={theme.colors.gray500}>
          공동 주최자는 이벤트 편집 권한을 공유합니다.
        </Text>

        <Flex justifyContent="space-between" gap="12px">
          <Button onClick={onClose} size="full" variant="outline">
            취소
          </Button>
          <Button onClick={handleApply} size="full" color="secondary">
            적용
          </Button>
        </Flex>
      </ModalBody>
    </Modal>
  );
};

const ModalBody = styled(Flex)`
  width: clamp(200px, 92vw, 320px);
  height: clamp(300px, 80vh, 450px);
  min-height: 0;
`;

const ScrollArea = styled.div`
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  padding-right: 4px;

  scrollbar-width: thin;
  &::-webkit-scrollbar {
    width: 8px;
  }
  &::-webkit-scrollbar-thumb {
    background: ${theme.colors.gray300};
    border-radius: 8px;
  }
`;

const Segment = styled.button<{ isSelected: boolean }>`
  all: unset;
  flex: 0 0 auto;
  word-break: keep-all;
  border: 1.5px solid
    ${(props) => (props.isSelected ? theme.colors.primary500 : theme.colors.gray300)};
  text-align: center;
  border-radius: 10px;
  cursor: pointer;
  padding: 6px 10px;
  white-space: nowrap;

  &:hover {
    border-color: ${theme.colors.primary500};
  }
`;
