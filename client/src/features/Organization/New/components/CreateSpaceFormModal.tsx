import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useSuspenseQuery } from '@tanstack/react-query';

import { organizationQueryOptions } from '@/api/queries/organization';
import { useSpaceJoinForm } from '@/features/Invite/hooks/useSpaceJoinForm';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { theme } from '@/shared/styles/theme';

type CreatorNicknameModalProps = {
  isOpen: boolean;
  orgName: string;
  previewUrl?: string;
  isSubmitting?: boolean;
  onConfirm: (data: { nickname: string; groupId: number }) => void;
  onClose: () => void;
};

export const CreateSpaceFormModal = ({
  isOpen,
  orgName,
  previewUrl,
  isSubmitting,
  onConfirm,
  onClose,
}: CreatorNicknameModalProps) => {
  const { error } = useToast();
  const { nickname, selectedGroup, handleNicknameChange, handleSelectGroup } = useSpaceJoinForm();
  const { data: organizationGroups } = useSuspenseQuery(organizationQueryOptions.group());

  const submit = () => {
    if (!selectedGroup) {
      error('그룹을 선택해주세요.');
      return;
    }

    onConfirm({ nickname, groupId: selectedGroup });
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 380px;
      `}
    >
      <Flex justifyContent="space-between" alignItems="baseline">
        <Text type="Heading" weight="bold" color="#333">
          멤버 정보 설정
        </Text>
      </Flex>

      <Flex dir="column" alignItems="flex-start" gap="14px">
        <Img src={previewUrl} alt={orgName} />
        <Text type="Body" weight="regular" color="#666">
          <Text as="span" type="Body" weight="bold" color={theme.colors.primary700}>
            그룹
          </Text>
          을 선택해주세요.
        </Text>
        <Flex
          css={css`
            flex-wrap: wrap;
          `}
          gap="8px"
          width="100%"
          justifyContent="center"
        >
          {organizationGroups.map((group) => (
            <Segment
              key={group.groupId}
              type="button"
              onClick={() => handleSelectGroup(group.groupId)}
              isSelected={selectedGroup === group.groupId}
              aria-pressed={selectedGroup === group.groupId}
            >
              <Text
                weight={selectedGroup === group.groupId ? 'bold' : 'regular'}
                color={
                  selectedGroup === group.groupId ? theme.colors.primary500 : theme.colors.gray500
                }
              >
                {group.name}
              </Text>
            </Segment>
          ))}
        </Flex>

        <Text type="Body" weight="regular" color="#666">
          <Text as="span" type="Body" weight="bold" color={theme.colors.primary700}>
            {orgName}
          </Text>
          에서 사용할 닉네임을 입력해주세요.
        </Text>
        <Input
          autoFocus
          id="nickname"
          type="text"
          placeholder="닉네임을 입력하세요"
          value={nickname}
          onChange={handleNicknameChange}
          showCounter
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              e.preventDefault();
              submit();
            }
          }}
        />
      </Flex>

      <Flex gap="12px" alignItems="center">
        <Button variant="outline" size="full" onClick={onClose} disabled={isSubmitting}>
          취소
        </Button>
        <Button size="full" disabled={!nickname.trim() || isSubmitting} onClick={submit}>
          생성하기
        </Button>
      </Flex>
    </Modal>
  );
};

const Segment = styled.button<{ isSelected: boolean }>`
  all: unset;
  flex: 0 0 auto;
  word-break: keep-all;
  border: 1.5px solid
    ${(props) => (props.isSelected ? theme.colors.primary500 : theme.colors.gray500)};
  text-align: center;
  border-radius: 8px;
  cursor: pointer;
  padding: 4px 8px;
  white-space: nowrap;
`;

const Img = styled.img`
  width: 100%;
  max-width: 250px;
  height: auto;
  margin: 0 auto;
  padding: 20px 0;
`;
