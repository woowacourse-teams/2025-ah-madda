import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQueries, useSuspenseQueries } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { myQueryOptions } from '@/api/queries/my';
import type { GuestAnswerAPIResponse } from '@/api/types/my';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { byOrderIndexAsc } from '../utils/sortByOrderIndex';

type PreAnswersSectionProps = {
  eventId: number;
};

export const PreAnswersSection = ({ eventId }: PreAnswersSectionProps) => {
  const [{ data: guests = [] }] = useSuspenseQueries({
    queries: [eventQueryOptions.guests(eventId)],
  });

  const shouldFetch = guests.length > 0;

  const answersResults = useQueries({
    queries: shouldFetch
      ? guests.map((g) => ({
          ...myQueryOptions.event.guestAnswers(eventId, g.guestId),
          select: (data: GuestAnswerAPIResponse[] | undefined) =>
            (data ?? []).slice().sort(byOrderIndexAsc),
          enabled: true,
        }))
      : [],
  });

  return (
    <Flex dir="column" gap="16px" padding="30px 0">
      <Text color={theme.colors.gray600}>참여자별 사전 답변 목록입니다.</Text>

      {guests.length === 0 && (
        <EmptyState>
          <Text type="Body" weight="medium" color={theme.colors.gray500}>
            아직 신청한 참여자가 없어요.
          </Text>
        </EmptyState>
      )}

      {guests.map((guest, idx) => {
        const result = answersResults[idx];
        const isLoading = result?.isLoading || result?.isFetching;
        const isError = !!result?.error;
        const items: GuestAnswerAPIResponse[] = result?.data ?? [];

        if (isLoading) {
          return (
            <Text
              key={guest.organizationMemberId}
              type="Label"
              weight="medium"
              color={theme.colors.gray500}
            >
              로딩 중...
            </Text>
          );
        }

        if (isError) {
          return (
            <Text
              key={guest.organizationMemberId}
              type="Label"
              weight="medium"
              color={theme.colors.gray800}
            >
              답변을 불러오지 못했습니다.
            </Text>
          );
        }

        if (items.length === 0) {
          return (
            <Text
              key={guest.organizationMemberId}
              type="Label"
              weight="medium"
              color={theme.colors.gray500}
            >
              작성한 답변이 없습니다.
            </Text>
          );
        }

        return (
          <Card key={guest.organizationMemberId}>
            <Text
              type="Heading"
              weight="bold"
              color={theme.colors.gray800}
              css={css`
                margin-bottom: 8px;
              `}
            >
              {guest.nickname}
            </Text>
            <QAList>
              {items.map((qa, i) => (
                <QAItem key={qa.orderIndex ?? i}>
                  <Text type="Body" weight="bold" color={theme.colors.gray800}>
                    {qa.questionText}
                  </Text>
                  <Text type="Body" weight="medium" color={theme.colors.gray700}>
                    {qa.answerText}
                  </Text>
                </QAItem>
              ))}
            </QAList>
          </Card>
        );
      })}
    </Flex>
  );
};

const Card = styled.article`
  border: 1px solid ${theme.colors.gray100};
  border-radius: 12px;
  padding: 16px;
`;

const QAList = styled(Flex)`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

const QAItem = styled.div`
  display: grid;
  padding: 8px 0;
`;

const EmptyState = styled.div`
  padding: 16px 0;
  text-align: center;
`;
