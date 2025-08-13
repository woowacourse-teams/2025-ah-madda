import { useState } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';

import { OrganizationImageInput } from './OrganizationImageInput';

export const OrganizationCreateForm = () => {
  const [logo, setLogo] = useState<File | null>(null);

  return (
    <form onSubmit={(e) => e.preventDefault()}>
      <Flex dir="column" gap="20px" padding="60px 0">
        <Text type="Title" weight="bold">
          조직 생성하기
        </Text>

        <Flex
          dir="column"
          gap="12px"
          width="100%"
          css={css`
            max-width: 260px;
          `}
        >
          <OrganizationImageInput onChange={setLogo} />
        </Flex>

        <Flex dir="column" gap="20px" width="100%">
          <Input
            id="orgName"
            name="orgName"
            label="조직 이름"
            placeholder="이벤트 이름을 입력해주세요"
          />

          {/* A.TODO: 추후 Textarea로 대체 예정 */}
          <Input
            id="orgDescription"
            name="orgDescription"
            label="소개글"
            placeholder="제목을 입력해주세요."
            helperText="5자 이상 입력하세요"
          />
        </Flex>

        <Button type="submit" color="primary" size="full">
          조직 생성하기
        </Button>
      </Flex>
    </form>
  );
};
