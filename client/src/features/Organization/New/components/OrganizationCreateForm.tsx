import { useCallback, useEffect, useRef, useState } from 'react';

import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import {
  getOrganizationDetailAPI,
  useUpdateOrganization,
} from '@/api/mutations/useUpdateOrganization';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { RequiredMark } from '@/shared/components/RequiredMark/RequiredMark';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';

import { MAX_LENGTH } from '../constants/validationRules';
import { useCreateOrganizationProcess } from '../hooks/useCreateOrganizationProcess';
import { useOrganizationForm } from '../hooks/useOrganizationForm';

import { CreatorNicknameModal } from './CreatorNicknameModal';
import { OrganizationImageInput } from './OrganizationImageInput';

export const OrganizationCreateForm = () => {
  const navigate = useNavigate();
  const { organizationId: paramId } = useParams();
  const organizationId = paramId ? Number(paramId) : undefined;
  const isEdit = !!organizationId;

  const { isOpen, open, close } = useModal();

  const [previewUrl, setPreviewUrl] = useState<string | undefined>(undefined);
  const objectUrlRef = useRef<string | undefined>(undefined);

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
      close();
      navigate(`/${newOrganizationId}/event`);
    },
    onClose: close,
  });

  const isSubmitting = isEdit ? isPatching : isCreating;

  const handleEditButtonClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (!isValid()) return;

    if (isEdit && organizationId) {
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

    open();
  };

  const handleConfirmNickname = (nickname: string) => {
    const trimmed = nickname.trim();
    if (!trimmed || isSubmitting) return;
    handleCreate(trimmed);
  };

  return (
    <>
      <Flex dir="column" padding="60px 0" gap="40px">
        <Flex padding="40px 0">
          <Text as="h1" type="Display" weight="bold">
            {isEdit ? '이벤트 스페이스 수정하기' : '이벤트 스페이스 생성하기'}
          </Text>
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
              <RequiredMark />
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
              <RequiredMark />
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
              <RequiredMark />
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
          isOpen={isOpen}
          orgName={form.name || '이벤트 스페이스'}
          previewUrl={previewUrl}
          isSubmitting={isSubmitting}
          onClose={close}
          onConfirm={handleConfirmNickname}
        />
      )}
    </>
  );
};
