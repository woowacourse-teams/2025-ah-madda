import { useEffect, useState } from 'react';

import { css } from '@emotion/react';

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

  useEffect(() => {
    const init = (members || []).map<NonGuest>((m) => ({
      organizationMemberId: m.organizationMemberId,
      nickname: m.nickname,
      imageUrl: undefined,
      isChecked: initialSelectedIds.includes(m.organizationMemberId),
    }));
    setCohostList(init);
  }, [isOpen, members, initialSelectedIds]);

  const selectedCount = cohostList.filter((m) => m.isChecked).length;

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
    const allChecked = cohostList.length > 0 && cohostList.every((m) => m.isChecked);
    setCohostList((prev) => {
      const toggled = prev.map((m) => ({ ...m, isChecked: !allChecked }));
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
        css={css`
          width: 560px;
          max-width: 92vw;
        `}
      >
        <Text as="label" type="Heading" weight="medium">
          {title}
        </Text>

        <GuestList
          title={`${title} (${selectedCount}명)`}
          titleColor={theme.colors.gray700}
          guests={cohostList}
          onGuestChecked={onGuestChecked}
          onAllGuestChecked={onAllGuestChecked}
        />

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
      </Flex>
    </Modal>
  );
};
