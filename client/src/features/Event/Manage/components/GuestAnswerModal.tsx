import { css } from '@emotion/react';

import type { GuestAnswerAPIResponse } from '@/api/types/my';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
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
  if (!guest || !guestAnswers) return null;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 380px;
      `}
    >
      <Flex dir="column" gap="24px" width="100%">
        <Text type="Heading" weight="bold" color={theme.colors.gray900}>
          {guest.nickname}님의 답변
        </Text>

        <Flex dir="column" gap="16px">
          {guestAnswers.length > 0 ? (
            <Flex dir="column" gap="16px">
              {guestAnswers.map((answer: GuestAnswerAPIResponse, index: number) => (
                <Flex key={index} dir="column" gap="8px">
                  <Text type="Body" weight="medium" color={theme.colors.gray900}>
                    질문 {answer.orderIndex + 1}: {answer.questionText}
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
        </Flex>

        <Flex justifyContent="center">
          <Button color="secondary" variant="outline" size="full" onClick={onClose}>
            닫기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
