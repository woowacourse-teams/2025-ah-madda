import { useState } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';
import { Textarea } from '@/shared/components/Textarea';

import { OrganizationImageInput } from './OrganizationImageInput';

export const OrganizationCreateForm = () => {
  const [logo, setLogo] = useState<File | null>(null);

  return (
    <form onSubmit={(e) => e.preventDefault()}>
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
            <OrganizationImageInput onChange={setLogo} />
          </Flex>

          <Flex dir="column" gap="12px">
            <label htmlFor="orgName">
              <Text type="Heading" weight="medium">
                조직 이름
              </Text>
            </label>
            <Input id="orgName" name="orgName" placeholder="조직 이름을 입력해주세요" />
          </Flex>

          <Flex dir="column" gap="12px">
            <label htmlFor="orgDescription">
              <Text type="Heading" weight="medium">
                소개글
              </Text>
            </label>
            <Textarea
              id="orgDescription"
              name="orgDescription"
              placeholder="제목을 입력해주세요."
              helperText="5자 이상 입력하세요"
            />
          </Flex>

          <Button type="submit" color="primary" size="full">
            조직 생성하기
          </Button>
        </Flex>
      </Flex>
    </form>
  );
};
