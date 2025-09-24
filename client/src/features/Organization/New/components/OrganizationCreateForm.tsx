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
import { GuestList } from '@/features/Event/Manage/components/GuestList';
import type { NonGuest } from '@/features/Event/Manage/types';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
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

type GroupKey = 'ALL' | number;
type GroupSegment = { id: GroupKey; name: string };

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
      } catch (e) {
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
        navigate('/organization');
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

  const [adminList, setAdminList] = useState<NonGuest[]>([]);
  const initialAdminIdsRef = useRef<Set<number>>(new Set());

  const [activeGroup, setActiveGroup] = useState<GroupKey>('ALL');

  const memberIdToGroupId = useMemo(() => {
    const map = new Map<number, number>();
    (members || []).forEach((m) => map.set(m.organizationMemberId, m.group.groupId));
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
    if (activeGroup === 'ALL') return adminList.map((m) => m.organizationMemberId);
    return adminList
      .map((m) => m.organizationMemberId)
      .filter((id) => memberIdToGroupId.get(id) === activeGroup);
  }, [adminList, activeGroup, memberIdToGroupId]);

  const filteredAdmins = useMemo(
    () =>
      adminList.filter((m) =>
        activeGroup === 'ALL' ? true : memberIdToGroupId.get(m.organizationMemberId) === activeGroup
      ),
    [adminList, activeGroup, memberIdToGroupId]
  );

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

  const onAdminChecked = (organizationMemberId: number) => {
    setAdminList((prev) =>
      prev.map((m) =>
        m.organizationMemberId === organizationMemberId ? { ...m, isChecked: !m.isChecked } : m
      )
    );
  };

  const onAdminAllChecked = () => {
    const allVisibleChecked = filteredAdmins.length > 0 && filteredAdmins.every((m) => m.isChecked);
    setAdminList((prev) =>
      prev.map((m) =>
        visibleIds.includes(m.organizationMemberId) ? { ...m, isChecked: !allVisibleChecked } : m
      )
    );
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

          <Flex dir="column" gap="12px">
            <Text as="label" htmlFor="orgName" type="Heading" weight="medium">
              이벤트 스페이스 이름
              <StyledRequiredMark>*</StyledRequiredMark>
            </Text>
            <Input
              id="orgName"
              name="name"
              placeholder="이벤트 스페이스 이름을 입력해주세요."
              value={form.name}
              onChange={handleChange}
              errorMessage={errors.name}
              showCounter
              maxLength={MAX_LENGTH.NAME}
              isRequired
            />
          </Flex>

          <Flex dir="column" gap="12px">
            <Text as="label" htmlFor="orgDescription" type="Heading" weight="medium">
              한 줄 소개
              <StyledRequiredMark>*</StyledRequiredMark>
            </Text>
            <Input
              id="orgDescription"
              name="description"
              placeholder="이벤트 스페이스를 소개해주세요."
              value={form.description}
              onChange={handleChange}
              errorMessage={errors.description}
              showCounter
              maxLength={MAX_LENGTH.DESCRIPTION}
              isRequired
            />
          </Flex>

          {isEdit && (
            <Flex dir="column" gap="12px">
              <Text as="label" type="Heading" weight="medium">
                관리자
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
                  title={`관리자 지정 (${selectedAdminIds.length}명)`}
                  titleColor={theme.colors.gray700}
                  guests={filteredAdmins}
                  onGuestChecked={onAdminChecked}
                  onAllGuestChecked={onAdminAllChecked}
                />
              </ScrollArea>

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
  min-height: 150px;
  max-height: 320px;
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
