import { css } from '@emotion/react';
import styled from '@emotion/styled';

import type { GuestAnswerAPIResponse } from '@/api/types/my';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import type { Guest } from '../types';

type GuestAnswerModalProps = {
  isOpen: boolean;
  onClose: () => void;
  guest: Guest | null;
  guestAnswers?: GuestAnswerAPIResponse[];
};

export const GuestAnswerModal = ({
  isOpen,
  onClose,
  guest,
  guestAnswers,
}: GuestAnswerModalProps) => {
  if (!guest) return null;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 400px;
        @media (max-width: 768px) {
          width: 90vw;
          max-width: 400px;
        }
      `}
    >
      <Flex dir="column" gap="24px" width="100%">
        <Text type="Heading" weight="bold" color={theme.colors.gray900}>
          {guest.nickname}님의 답변
        </Text>

        <StyledFlex dir="column" gap="16px">
          {guestAnswers && guestAnswers.length > 0 ? (
            <Flex dir="column" gap="16px">
              {guestAnswers.map((answer: GuestAnswerAPIResponse, index: number) => (
                <Flex key={index} dir="column" gap="8px">
                  <Text type="Body" weight="medium" color={theme.colors.gray900}>
                    질문 {answer.orderIndex}: {answer.questionText}
                  </Text>
                  <Text type="Body" weight="regular" color={theme.colors.gray600}>
                    답변: {answer.answerText}
                  </Text>
                </Flex>
              ))}
            </Flex>
          ) : (
            <Text type="Body" weight="regular" color={theme.colors.gray600}>
              사전 질문에 대한 답변이 존재하지 않습니다.
            </Text>
          )}
        </StyledFlex>

        <Spacing height="1px" />

        <Flex justifyContent="center">
          <Button color="secondary" variant="outline" size="full" onClick={onClose}>
            닫기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};

const StyledFlex = styled(Flex)`
  max-height: 400px;
  overflow-y: auto;
  padding-right: 4px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: ${theme.colors.gray300};
    border-radius: 3px;

    &:hover {
      background: ${theme.colors.gray400};
    }
  }
`;
