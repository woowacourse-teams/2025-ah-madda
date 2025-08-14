import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { Answer } from '../../../../../api/types/event';
import type { EventDetail } from '../../../types/Event';
import { QuestionContainer } from '../../containers/QuestionContainer';

type PreQuestionSectionProps = Pick<EventDetail, 'questions'> & {
  answers: Answer[];
  onChangeAnswer: (questionId: number, answerText: string) => void;
};

export const PreQuestionSection = ({
  questions,
  answers,
  onChangeAnswer,
}: PreQuestionSectionProps) => {
  return (
    <QuestionContainer>
      <Flex dir="column" gap="16px">
        <Flex dir="column" gap="8px">
          <Text type="Heading" weight="bold">
            사전 질문
          </Text>
          <Text type="Body">아래 질문에 대한 답변을 작성해주세요.</Text>
        </Flex>
        <Flex dir="column" gap="24px">
          {/* S.TODO : Input 디자인 변경 이후 수정 예정 */}
          {questions.map((question) => (
            <Flex key={question.questionId} dir="column" gap="4px">
              <Text type="Body" weight="semibold">
                {question.questionText}
                {question.isRequired && <span style={{ color: 'red' }}> *</span>}
              </Text>
              <Input
                id={`question-${question.questionId}`}
                placeholder="답변을 입력하세요"
                value={answers.find((a) => a.questionId === question.questionId)?.answerText ?? ''}
                onChange={(e) => onChangeAnswer(question.questionId, e.target.value)}
                css={css`
                  outline: 1px solid ${theme.colors.gray300};
                `}
              />
            </Flex>
          ))}
        </Flex>
      </Flex>
    </QuestionContainer>
  );
};
