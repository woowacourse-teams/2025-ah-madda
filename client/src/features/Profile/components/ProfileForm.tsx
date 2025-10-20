import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { OrganizationGroupAPIResponse } from '@/api/types/organizations';
import { Profile } from '@/api/types/profile';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { useProfileForm } from '../hooks/useProfileForm';

type ProfileFormProps = {
  profile: Profile;
  group: OrganizationGroupAPIResponse[];
};

export const ProfileForm = ({ profile, group }: ProfileFormProps) => {
  const {
    nickname,
    selectedGroup,
    handleNicknameChange,
    handleGroupChange,
    // handleSaveProfile,
    hasChanges,
    // isLoading,
  } = useProfileForm({
    initialNickname: profile.name,
    initialGroupID: profile.group?.groupId ?? 4,
  });
  console.log(profile.group);

  return (
    <>
      <Flex dir="column" gap="24px" width="100%">
        <Flex dir="column" gap="8px">
          <Text type="Heading" weight="semibold" color="gray700">
            그룹
          </Text>
          <Flex
            gap="8px"
            width="100%"
            justifyContent="flex-start"
            css={css`
              flex-wrap: wrap;
            `}
          >
            {group.map((group) => (
              <Segment
                key={group.groupId}
                type="button"
                onClick={() => handleGroupChange(group.groupId)}
                isSelected={selectedGroup === group.groupId}
                aria-pressed={selectedGroup === group.groupId}
              >
                <Text
                  weight={selectedGroup === group.groupId ? 'bold' : 'regular'}
                  color={
                    selectedGroup === group.groupId ? theme.colors.primary500 : theme.colors.gray300
                  }
                >
                  {group.name}
                </Text>
              </Segment>
            ))}
          </Flex>
        </Flex>
        <Flex dir="column" gap="8px">
          <Text type="Heading" weight="semibold" color="gray700">
            닉네임
          </Text>
          <Input
            id="nickname"
            value={nickname}
            onChange={handleNicknameChange}
            placeholder="닉네임을 입력하세요"
          />
        </Flex>
      </Flex>

      <Button
        type="button"
        size="full"
        // disabled={!hasChanges || isLoading}
        // onClick={handleSaveProfile}
        // isLoading={isLoading}
      >
        프로필 수정
      </Button>
    </>
  );
};

const Segment = styled.button<{ isSelected: boolean }>`
  all: unset;
  flex: 0 0 auto;
  word-break: keep-all;
  border: 1.5px solid
    ${(props) => (props.isSelected ? theme.colors.primary500 : theme.colors.gray300)};
  text-align: center;
  border-radius: 8px;
  cursor: pointer;
  padding: 4px 8px;
  white-space: nowrap;
`;
