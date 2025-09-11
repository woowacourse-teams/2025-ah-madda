import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';

type ProfileFormProps = {
  nickname: string;
  email: string;
  hasChanges: boolean;
  isLoading: boolean;
  onNicknameChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSave: () => void;
};

export const ProfileForm = ({
  nickname,
  email,
  hasChanges,
  isLoading,
  onNicknameChange,
  onSave,
}: ProfileFormProps) => {
  return (
    <>
      <Flex dir="column" gap="8px">
        <Text type="Body" weight="medium" color="gray700">
          닉네임
        </Text>
        <Input
          id="nickname"
          value={nickname}
          onChange={onNicknameChange}
          placeholder="닉네임을 입력하세요"
        />
      </Flex>

      <Flex dir="column" gap="8px">
        <Text type="Body" weight="medium" color="gray700">
          이메일
        </Text>
        <Input
          id="email"
          value={email}
          disabled
          css={css`
            cursor: not-allowed;
          `}
        />
      </Flex>

      <Spacing height="24px" />

      <Flex padding="0 16px">
        <Button
          type="button"
          size="lg"
          disabled={!hasChanges || isLoading}
          css={css`
            width: 100%;
          `}
          onClick={onSave}
        >
          {isLoading ? '저장 중...' : '변경사항 저장'}
        </Button>
      </Flex>
    </>
  );
};
