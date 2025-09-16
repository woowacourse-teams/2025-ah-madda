import { useCallback, useEffect, useRef, useState } from 'react';

import { css } from '@emotion/react';
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

import { CreatorNicknameModal } from './CreatorNicknameModal';
import { OrganizationDeleteModal } from './OrganizationDeleteModal';
import { OrganizationImageInput } from './OrganizationImageInput';

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
      if (objectUrlRef.current) {
        URL.revokeObjectURL(objectUrlRef.current);
      }
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

  const handleEditButtonClick = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (!isValid()) return;

    if (isEdit && organizationId) {
      let nextAdminIds = adminList.filter((m) => m.isChecked).map((m) => m.organizationMemberId);

      const myId = myProfile?.organizationMemberId;
      if (myId) {
        nextAdminIds = [...nextAdminIds, myId];
      }

      const prev = initialAdminIdsRef.current;
      const next = new Set(nextAdminIds);
      const added: number[] = [];
      const removed: number[] = [];

      next.forEach((id) => {
        if (!prev.has(id)) added.push(id);
      });
      prev.forEach((id) => {
        if (!next.has(id)) removed.push(id);
      });

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

  const handleConfirmNickname = (nickname: string) => {
    const trimmed = nickname.trim();
    if (!trimmed || isSubmitting) return;
    handleCreate(trimmed);
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
    setAdminList((prev) => {
      const allChecked = prev.length > 0 && prev.every((m) => m.isChecked);
      return prev.map((m) => ({ ...m, isChecked: !allChecked }));
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

              <GuestList
                title={`관리자 지정 (${selectedAdminIds.length}명)`}
                titleColor={theme.colors.gray700}
                guests={adminList}
                onGuestChecked={onAdminChecked}
                onAllGuestChecked={onAdminAllChecked}
              />

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
        <CreatorNicknameModal
          isOpen={createModal.isOpen}
          orgName={form.name || '이벤트 스페이스'}
          previewUrl={previewUrl}
          isSubmitting={isSubmitting}
          onClose={createModal.close}
          onConfirm={handleConfirmNickname}
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
