import { useState } from 'react';

import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';

import { MAX_LENGTH } from '../constants/errorMessages';
import { useOrganizationForm } from '../hooks/useOrganizationForm';

import { CreatorNicknameModal } from './CreatorNicknameModal';
import { OrganizationImageInput } from './OrganizationImageInput';

export const OrganizationCreateForm = () => {
  const navigate = useNavigate();
  const [isNickOpen, setNickOpen] = useState(false);

  const { form, errors, isValid, handleChange, handleLogoChange } = useOrganizationForm();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isValid()) return;
    setNickOpen(true);
  };

  return (
    <form onSubmit={handleSubmit}>
      <Flex dir="column" padding="60px 0" gap="40px">
        <Flex padding="40px 0">
          <Text as="h1" type="Display" weight="bold">
            조직 생성하기
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
              조직 이미지
            </Text>
            <OrganizationImageInput onChange={handleLogoChange} errorMessage={errors.logo} />
          </Flex>

          <Flex dir="column" gap="12px">
            <label htmlFor="orgName">
              <Text type="Heading" weight="medium">
                조직 이름
              </Text>
            </label>
            <Input
              id="orgName"
              name="name"
              placeholder="조직 이름을 입력해주세요."
              value={form.name}
              onChange={handleChange}
              errorMessage={errors.name}
              showCounter
              maxLength={MAX_LENGTH.NAME}
              isRequired
            />
          </Flex>

          <Flex dir="column" gap="12px">
            <label htmlFor="orgDescription">
              <Text type="Heading" weight="medium">
                한 줄 소개
              </Text>
            </label>
            <Input
              id="orgDescription"
              name="description"
              placeholder="조직을 소개해주세요."
              value={form.description}
              onChange={handleChange}
              errorMessage={errors.description}
              showCounter
              maxLength={MAX_LENGTH.DESCRIPTION}
              isRequired
            />
          </Flex>

          <Button type="submit" color="primary" size="full" disabled={!isValid()}>
            조직 생성하기
          </Button>
        </Flex>
      </Flex>

      <CreatorNicknameModal
        isOpen={isNickOpen}
        orgName={form.name || '조직'}
        name={form.name.trim()}
        description={form.description.trim()}
        thumbnail={form.logo}
        onCancel={() => setNickOpen(false)}
        onSuccess={(id) => {
          setNickOpen(false);
          // A.TODO: 추후 페이지 이동 예정
          // navigate(`/organization/${id}`);
        }}
      />
    </form>
  );
};
