import { useCallback, useEffect, useMemo, useRef, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useDeleteOrganization } from '@/api/mutations/useDeleteOrganization';
import {
  getOrganizationDetailAPI,
  useUpdateOrganization,
} from '@/api/mutations/useUpdateOrganization';
import { useUpdateOrganizationMemberRoles } from '@/api/mutations/useUpdateOrganizationMemberRoles';
import { organizationQueryOptions } from '@/api/queries/organization';
import { GuestList } from '@/features/Event/Manage/components/guest/GuestList';
import type { NonGuest } from '@/features/Event/Manage/types';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Spacing } from '@/shared/components/Spacing';
import { Tabs } from '@/shared/components/Tabs/Tabs';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';
import { theme } from '@/shared/styles/theme';

import { MAX_LENGTH } from '../constants/validationRules';
import { useCreateOrganizationProcess } from '../hooks/useCreateOrganizationProcess';
import { useOrganizationForm } from '../hooks/useOrganizationForm';

import { CreateSpaceFormModal } from './CreateSpaceFormModal';
import { OrganizationDeleteModal } from './OrganizationDeleteModal';
import { OrganizationImageInput } from './OrganizationImageInput';

type TabId = 'ADMIN' | number;
type UITab = { id: TabId; name: string };

export const OrganizationCreateForm = () => {
  const navigate = useNavigate();
  const { organizationId: paramId } = useParams();
  const organizationId = Number(paramId);
  const isEdit = !!organizationId;

  const { error, success } = useToast();
  const { mutate: deleteOrganization } = useDeleteOrganization();
  const createModal = useModal();
  const deleteModal = useModal();

  const [previewUrl, setPreviewUrl] = useState<string | undefined>(undefined);
  const objectUrlRef = useRef<string | undefined>(undefined);
  const { mutateAsync: updateRoles } = useUpdateOrganizationMemberRoles();

  const { data: org } = useQuery({
    queryKey: ['organization', 'detail', organizationId],
    queryFn: () => getOrganizationDetailAPI(Number(organizationId)),
    enabled: isEdit && !!organizationId,
  });

  const { form, errors, isValid, handleChange, handleLogoChange, loadFormData } =
    useOrganizationForm(undefined, { requireThumbnail: !isEdit });

  const initOrgForm = useCallback(() => {
    if (!isEdit || !org) return;
    loadFormData({ name: org.name, description: org.description });
    setPreviewUrl(org.imageUrl ?? undefined);
  }, [isEdit, org, loadFormData]);

  useEffect(() => {
    initOrgForm();
  }, [initOrgForm]);

  const onSelectLogo = (file: File | null) => {
    if (objectUrlRef.current) {
      URL.revokeObjectURL(objectUrlRef.current);
      objectUrlRef.current = undefined;
    }
    if (file) {
      const url = URL.createObjectURL(file);
      objectUrlRef.current = url;
      setPreviewUrl(url);
    } else {
      setPreviewUrl(isEdit ? (org?.imageUrl ?? undefined) : undefined);
    }
    handleLogoChange(file);
  };

  useEffect(() => {
    return () => {
      if (objectUrlRef.current) URL.revokeObjectURL(objectUrlRef.current);
    };
  }, []);

  const { mutate: patchOrganization, isPending: isPatching } = useUpdateOrganization();

  const { handleCreate, isSubmitting: isCreating } = useCreateOrganizationProcess({
    name: form.name.trim(),
    description: form.description.trim(),
    thumbnail: form.thumbnail,
    onSuccess: (newOrganizationId: number) => {
      createModal.close();
      navigate(`/${newOrganizationId}/event`);
    },
    onClose: createModal.close,
  });

  const isSubmitting = isEdit ? isPatching : isCreating;

  const setEquals = (next: Set<number>, prev: Set<number>) => {
    if (next.size !== prev.size) return false;
    for (const v of next) if (!prev.has(v)) return false;
    return true;
  };

  const handleEditButtonClick = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (!isValid()) return;

    if (isEdit && organizationId) {
      let nextAdminIds = adminList.filter((m) => m.isChecked).map((m) => m.organizationMemberId);
      const myId = myProfile?.organizationMemberId;
      if (myId) nextAdminIds = [...nextAdminIds, myId];

      const prev = initialAdminIdsRef.current;
      const next = new Set(nextAdminIds);

      if ((!next.size && !prev.size) || setEquals(next, prev)) {
        patchOrganization(
          {
            organizationId,
            payload: {
              organization: {
                name: form.name.trim(),
                description: form.description.trim(),
              },
              thumbnail: form.thumbnail ?? null,
            },
          },
          { onSuccess: () => navigate(`/${organizationId}/event`) }
        );
        return;
      }

      const added: number[] = [];
      const removed: number[] = [];
      next.forEach((id) => {
        if (!prev.has(id)) added.push(id);
      });
      prev.forEach((id) => {
        if (!next.has(id)) removed.push(id);
      });

      try {
        await Promise.all([
          added.length
            ? updateRoles({
                organizationId,
                payload: { organizationMemberIds: added, role: 'ADMIN' },
              })
            : Promise.resolve(),
          removed.length
            ? updateRoles({
                organizationId,
                payload: { organizationMemberIds: removed, role: 'USER' },
              })
            : Promise.resolve(),
        ]);

        success(`관리자 권한이 업데이트되었습니다.`);
        initialAdminIdsRef.current = new Set(next);

        patchOrganization(
          {
            organizationId,
            payload: {
              organization: {
                name: form.name.trim(),
                description: form.description.trim(),
              },
              thumbnail: form.thumbnail ?? null,
            },
          },
          {
            onSuccess: () => {
              navigate(`/${organizationId}/event`);
            },
          }
        );
      } catch {
        error('권한 업데이트에 실패했습니다.');
      }
      return;
    }

    createModal.open();
  };

  const handleDeleteButtonClick = () => {
    deleteOrganization(organizationId, {
      onSuccess: () => {
        success('이벤트 스페이스가 성공적으로 삭제되었습니다!');
        deleteModal.close();
        navigate('/');
      },
      onError: (err) => {
        if (err instanceof HttpError) {
          error(err.message || '이벤트 스페이스 삭제에 실패했어요.');
        }
      },
    });
  };

  const handleSpaceOneForm = (data: { nickname: string; groupId: number }) => {
    if (!data.nickname.trim() || isSubmitting) return;
    handleCreate(data);
  };

  const { data: myProfile } = useQuery({
    ...organizationQueryOptions.profile(Number(organizationId)),
    enabled: isEdit && !!organizationId,
  });

  const { data: members } = useQuery({
    ...organizationQueryOptions.members(Number(organizationId)),
    enabled: !!organizationId,
  });

  const { data: organizationGroups } = useQuery({
    ...organizationQueryOptions.group(),
    enabled: !!organizationId,
  });

  const [adminList, setAdminList] = useState<NonGuest[]>([]);
  const initialAdminIdsRef = useRef<Set<number>>(new Set());

  useEffect(() => {
    if (!members) return;

    const myId = myProfile?.organizationMemberId;
    const list = members
      .filter((member) => (myId ? member.organizationMemberId !== myId : true))
      .map((member) => ({
        organizationMemberId: member.organizationMemberId,
        nickname: member.nickname,
        imageUrl: undefined,
        isChecked: !!member.isAdmin,
      }));

    setAdminList(list);
    initialAdminIdsRef.current = new Set(
      members.filter((m) => m.isAdmin).map((m) => m.organizationMemberId)
    );
  }, [members, myProfile]);

  const memberIdToGroupId = useMemo(() => {
    const map = new Map<number, number>();
    (members || []).forEach((m) => map.set(m.organizationMemberId, m.group.groupId));
    return map;
  }, [members]);

  const tabs: UITab[] = useMemo(() => {
    const groupTabs =
      (organizationGroups || [])
        .map((g: { groupId: number; name: string }) => ({ id: g.groupId as TabId, name: g.name }))
        .sort((a, b) => Number(a.id) - Number(b.id)) ?? [];
    return [{ id: 'ADMIN' as TabId, name: '관리자' }, ...groupTabs];
  }, [organizationGroups]);

  const idsOfTab = (tabId: TabId) => {
    if (tabId === 'ADMIN') {
      return adminList.filter((m) => m.isChecked).map((m) => m.organizationMemberId);
    }
    return adminList
      .filter((m) => memberIdToGroupId.get(m.organizationMemberId) === tabId)
      .map((m) => m.organizationMemberId);
  };

  const guestsOfTab = (tabId: TabId) => {
    if (tabId === 'ADMIN') {
      return adminList.filter((m) => m.isChecked);
    }
    return adminList.filter((m) => memberIdToGroupId.get(m.organizationMemberId) === tabId);
  };

  const onAdminChecked = (organizationMemberId: number) => {
    setAdminList((prev) =>
      prev.map((m) =>
        m.organizationMemberId === organizationMemberId ? { ...m, isChecked: !m.isChecked } : m
      )
    );
  };

  const onAllCheckedFor = (visibleIds: number[]) => {
    setAdminList((prev) => {
      const visible = prev.filter((m) => visibleIds.includes(m.organizationMemberId));
      const allVisibleChecked = visible.length > 0 && visible.every((m) => m.isChecked);
      return prev.map((m) =>
        visibleIds.includes(m.organizationMemberId) ? { ...m, isChecked: !allVisibleChecked } : m
      );
    });
  };

  const selectedAdminIds = adminList.filter((m) => m.isChecked).map((m) => m.organizationMemberId);

  return (
    <>
      <Flex dir="column" padding="60px 0" gap="40px">
        <Flex justifyContent="space-between" alignItems="center" padding="40px 0">
          <Text as="h1" type="Display" weight="bold">
            {isEdit ? '이벤트 스페이스 수정하기' : '이벤트 스페이스 생성하기'}
          </Text>
          {isEdit && (
            <Button
              size="sm"
              onClick={deleteModal.open}
              css={css`
                background-color: ${theme.colors.red100};
                color: ${theme.colors.red400};

                &:hover {
                  background-color: ${theme.colors.red300};
                  color: ${theme.colors.white};
                }
              `}
            >
              삭제
            </Button>
          )}
        </Flex>

        <Flex dir="column" gap="40px" width="100%">
          <Flex
            dir="column"
            gap="12px"
            css={css`
              max-width: 260px;
            `}
          >
            <Text as="label" htmlFor="orgImage" type="Heading" weight="medium">
              이벤트 스페이스 이미지
              <StyledRequiredMark>*</StyledRequiredMark>
            </Text>
            <OrganizationImageInput
              onChange={onSelectLogo}
              errorMessage={errors.thumbnail}
              initialPreviewUrl={org?.imageUrl ?? undefined}
            />
          </Flex>

          <Flex dir="column">
            <Text as="label" htmlFor="orgName" type="Heading" weight="medium">
              이벤트 스페이스 이름
              <StyledRequiredMark>*</StyledRequiredMark>
            </Text>
            <Spacing height="8px" />
            <Input
              id="orgName"
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="이벤트 스페이스 이름을 입력해주세요."
              errorMessage={errors.name}
              showCounter
              isRequired
              maxLength={MAX_LENGTH.NAME}
            />
          </Flex>

          <Flex dir="column">
            <Text as="label" htmlFor="orgDescription" type="Heading" weight="medium">
              한 줄 소개
              <StyledRequiredMark>*</StyledRequiredMark>
            </Text>
            <Spacing height="8px" />
            <Input
              id="orgDescription"
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="이벤트 스페이스를 소개해주세요."
              errorMessage={errors.description}
              showCounter
              isRequired
              maxLength={MAX_LENGTH.DESCRIPTION}
            />
          </Flex>

          {isEdit && (
            <Flex dir="column" gap="12px">
              <Text as="label" type="Heading" weight="medium">
                관리자
              </Text>

              <Tabs defaultValue={String(tabs[0]?.id ?? '')}>
                <Tabs.List
                  css={css`
                    --tabs-gap: 12px;

                    display: flex;
                    overflow-x: auto;
                    white-space: nowrap;
                    padding: 0 4px;
                    column-gap: var(--tabs-gap);

                    @media (max-width: 480px) {
                      --tabs-gap: 8px;
                      padding: 0 2px;
                    }
                  `}
                >
                  {tabs.map((t) => (
                    <Tabs.Trigger
                      key={t.id}
                      value={String(t.id)}
                      css={css`
                        margin: 0;
                        padding: 6px 10px;

                        @media (max-width: 500px) {
                          padding: 3px 6px;
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
                    t.id === 'ADMIN'
                      ? '관리자 권한을 가진 사용자가 없어요.'
                      : '이 그룹에 속해있는 구성원이 없어요.';

                  return (
                    <Tabs.Content key={`${t.id}`} value={String(t.id)}>
                      <ScrollArea>
                        {guests.length === 0 ? (
                          <EmptyState>{emptyMsg}</EmptyState>
                        ) : (
                          <GuestList
                            title={`관리자 지정 (${selectedAdminIds.length}명)`}
                            titleColor={theme.colors.gray700}
                            guests={guests}
                            onGuestChecked={onAdminChecked}
                            onAllGuestChecked={() => onAllCheckedFor(ids)}
                          />
                        )}
                      </ScrollArea>
                    </Tabs.Content>
                  );
                })}
              </Tabs>

              <Text type="Label" color={theme.colors.gray500}>
                이벤트 스페이스 생성자는 자동으로 관리자 권한을 갖습니다.
              </Text>
            </Flex>
          )}

          <Button
            type="submit"
            color="primary"
            size="full"
            disabled={!isValid() || isSubmitting}
            onClick={handleEditButtonClick}
          >
            {isEdit ? '이벤트 스페이스 수정하기' : '이벤트 스페이스 생성하기'}
          </Button>
        </Flex>
      </Flex>

      {!isEdit && (
        <CreateSpaceFormModal
          isOpen={createModal.isOpen}
          orgName={form.name || '이벤트 스페이스'}
          previewUrl={previewUrl}
          isSubmitting={isSubmitting}
          onClose={createModal.close}
          onConfirm={handleSpaceOneForm}
        />
      )}

      <OrganizationDeleteModal
        isOpen={deleteModal.isOpen}
        onClose={deleteModal.close}
        onDeleteConfirm={handleDeleteButtonClick}
      />
    </>
  );
};

const StyledRequiredMark = styled.span`
  margin-left: 8px;
  color: ${theme.colors.red600};
`;

const ScrollArea = styled.div`
  min-height: 200px;
  max-height: 320px;
  overflow-y: auto;
  padding-right: 4px;

  scrollbar-width: thin;
`;

const EmptyState = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  color: ${theme.colors.gray800};
  font-size: 14px;
  height: 180px;
  text-align: center;
`;
