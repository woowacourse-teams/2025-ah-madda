import { useEffect, useMemo, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import type { OrganizationMember } from '@/api/types/organizations';
import { GuestList } from '@/features/Event/Manage/components/GuestList';
import type { NonGuest } from '@/features/Event/Manage/types';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Tabs } from '@/shared/components/Tabs/Tabs';
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

type TabId = 'HOST' | number;
type GroupTab = { id: TabId; name: string };

export const CoHostSelectModal = ({
  isOpen,
  members,
  initialSelectedIds,
  onClose,
  onSubmit,
  maxSelectable,
  title = '공동 주최자 지정',
}: CoHostSelectModalProps) => {
  const { organizationId } = useParams();
  const { error } = useToast();
  const [cohostList, setCohostList] = useState<NonGuest[]>([]);

  const { data: organizationGroups } = useQuery({
    ...organizationQueryOptions.group(),
    enabled: !!organizationId,
  });

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
    (members || []).forEach((m) => map.set(m.organizationMemberId, m.group.groupId));
    return map;
  }, [members]);

  const tabs: GroupTab[] = useMemo(() => {
    const groupTabs =
      (organizationGroups || [])
        .map((g: { groupId: number; name: string }) => ({
          id: g.groupId as TabId,
          name: g.name,
        }))
        .sort((a, b) => Number(a.id) - Number(b.id)) ?? [];
    return [{ id: 'HOST' as TabId, name: '주최자' }, ...groupTabs];
  }, [organizationGroups]);

  const idsOfTab = (tabId: TabId) => {
    if (tabId === 'HOST')
      return cohostList.filter((m) => m.isChecked).map((m) => m.organizationMemberId);
    return cohostList
      .filter((m) => memberIdToGroupId.get(m.organizationMemberId) === tabId)
      .map((m) => m.organizationMemberId);
  };

  const guestsOfTab = (tabId: TabId) => {
    if (tabId === 'HOST') return cohostList.filter((m) => m.isChecked);
    return cohostList.filter((m) => memberIdToGroupId.get(m.organizationMemberId) === tabId);
  };

  const totalSelected = cohostList.filter((m) => m.isChecked).length;

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

  const onAllGuestCheckedFor = (visibleIds: number[]) => {
    setCohostList((prev) => {
      const visible = prev.filter((m) => visibleIds.includes(m.organizationMemberId));
      const allChecked = visible.length > 0 && visible.every((m) => m.isChecked);
      const toggled = prev.map((m) =>
        visibleIds.includes(m.organizationMemberId) ? { ...m, isChecked: !allChecked } : m
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
      <Flex
        dir="column"
        gap="12px"
        width="clamp(200px, 85vw, 500px)"
        height="height: clamp(300px, 80vh, 450px)"
        css={css`
          min-height: 0;
        `}
      >
        <Text as="label" type="Heading" weight="medium">
          {title}
        </Text>

        <Tabs defaultValue={String(tabs[0]?.id ?? '')}>
          <Tabs.List>
            {tabs.map((t) => (
              <Tabs.Trigger
                key={t.id}
                value={String(t.id)}
                css={css`
                  margin: 0;
                  padding: 6px;

                  @media (max-width: 500px) {
                    font-size: 13px;
                  }
                `}
              >
                {t.name}
              </Tabs.Trigger>
            ))}
          </Tabs.List>

          {tabs.map((t) => {
            const guests = guestsOfTab(t.id);
            const ids = idsOfTab(t.id);
            const emptyMsg =
              t.id === 'HOST'
                ? '선택된 공동 주최자가 없어요.'
                : '이 그룹에 속해있는 구성원이 없어요.';

            return (
              <Tabs.Content
                key={`${t.id}`}
                value={String(t.id)}
                css={css`
                  height: clamp(300px, 30vh, 400px);
                `}
              >
                <ScrollArea>
                  {guests.length === 0 ? (
                    <EmptyState>{emptyMsg}</EmptyState>
                  ) : (
                    <GuestList
                      title={`${title} (${totalSelected}명)`}
                      titleColor={theme.colors.gray700}
                      guests={guests}
                      onGuestChecked={onGuestChecked}
                      onAllGuestChecked={() => onAllGuestCheckedFor(ids)}
                    />
                  )}
                </ScrollArea>
              </Tabs.Content>
            );
          })}
        </Tabs>

        <StickyFooter>
          <Text type="Label" color={theme.colors.gray500} css={{ marginBottom: 12 }}>
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
        </StickyFooter>
      </Flex>
    </Modal>
  );
};

const ScrollArea = styled.div`
  flex: 1 1 auto;

  overflow-y: auto;
  padding-right: 4px;
  margin-top: 12px;

  scrollbar-width: thin;
  &::-webkit-scrollbar {
    width: 8px;
  }
  &::-webkit-scrollbar-thumb {
    background: ${theme.colors.gray300};
    border-radius: 8px;
  }
`;

const StickyFooter = styled.div`
  margin-top: auto;
  background: ${theme.colors.white};
  padding-top: 8px;
`;

const EmptyState = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  color: ${theme.colors.gray400};
  font-size: 14px;
  height: 250px;
  text-align: center;
`;
