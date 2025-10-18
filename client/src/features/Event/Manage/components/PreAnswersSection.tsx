import styled from '@emotion/styled';
import { useQueries, useSuspenseQueries } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { myQueryOptions } from '@/api/queries/my';
import type { GuestAnswerAPIResponse } from '@/api/types/my';
import { Flex } from '@/shared/components/Flex';
import { Spacing } from '@/shared/components/Spacing';
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
      <Text color={theme.colors.gray600}>질문별로 각 참여자의 답변을 보여줍니다.</Text>

      {guests.length === 0 && (
        <EmptyState>
          <Text type="Body" weight="medium" color={theme.colors.gray500}>
            아직 신청한 참여자가 없어요.
          </Text>
        </EmptyState>
      )}

      {guests.length > 0 && (
        <>
          {(() => {
            type ParticipantAnswer = {
              nickname: string;
              organizationMemberId: number;
              answerText: string;
            };

            const answersByQuestion = new Map<
              number,
              { questionText: string; participants: ParticipantAnswer[] }
            >();

            answersResults.forEach((result, guestIndex) => {
              const guestAnswers: GuestAnswerAPIResponse[] = result?.data ?? [];
              const guest = guests[guestIndex];

              guestAnswers.forEach((qa) => {
                const orderIndex = qa.orderIndex;

                const questionGroup = answersByQuestion.get(orderIndex);
                const participantAnswer: ParticipantAnswer = {
                  nickname: guest.nickname,
                  organizationMemberId: guest.organizationMemberId,
                  answerText: qa.answerText,
                };

                if (questionGroup) {
                  questionGroup.participants.push(participantAnswer);
                } else {
                  answersByQuestion.set(orderIndex, {
                    questionText: qa.questionText,
                    participants: [participantAnswer],
                  });
                }
              });
            });

            const questions = Array.from(answersByQuestion.entries())
              .sort((a, b) => a[0] - b[0])
              .map(([orderIndex, group]) => ({ orderIndex, ...group }));

            if (questions.length === 0) {
              return (
                <EmptyState>
                  <Text type="Body" weight="medium" color={theme.colors.gray500}>
                    사전 질문에 대한 답변이 없습니다.
                  </Text>
                </EmptyState>
              );
            }

            return questions.map((q) => (
              <Card key={q.orderIndex}>
                <Text type="Heading" weight="bold" color={theme.colors.gray800}>
                  {q.questionText}
                </Text>
                <Spacing height="8px" />
                <QAList>
                  {q.participants.map((pa, idx) => (
                    <QAItem key={`${q.orderIndex}-${pa.organizationMemberId}-${idx}`}>
                      <Text type="Body" weight="bold" color={theme.colors.gray800}>
                        {pa.nickname}
                      </Text>
                      <Text type="Body" weight="medium" color={theme.colors.gray700}>
                        {pa.answerText}
                      </Text>
                    </QAItem>
                  ))}
                </QAList>
              </Card>
            ));
          })()}
        </>
      )}
    </Flex>
  );
};

const Card = styled.article`
  border: 1px solid ${theme.colors.gray100};
  border-radius: 12px;
  padding: 16px;
`;

const QAList = styled(Flex)`
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
