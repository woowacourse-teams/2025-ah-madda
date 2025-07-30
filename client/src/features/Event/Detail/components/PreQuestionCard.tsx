import { Answer } from '../../../../api/types/event';
import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Input } from '../../../../shared/components/Input';
import { Text } from '../../../../shared/components/Text';
import type { EventDetail } from '../../../Event/types/Event';

type PreQuestionCardProps = Pick<EventDetail, 'questions'> & {
  answers: Answer[];
  onChangeAnswer: (questionId: number, answerText: string) => void;
};

export const PreQuestionCard = ({ questions, answers, onChangeAnswer }: PreQuestionCardProps) => {
  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Text type="Body">사전 질문</Text>
        Label
        <Flex dir="column" gap="24px">
          {questions.map((question) => (
            <Flex key={question.questionId} dir="column" gap="4px">
              <label htmlFor={`question-${question.questionId}`}>
                <Text type="Label" weight="bold">
                  {question.questionText}
                  {question.isRequired && <span style={{ color: 'red' }}> *</span>}
                </Text>
              </label>
              <Input
                id={`question-${question.questionId}`}
                label=""
                placeholder="답변을 입력하세요"
                value={answers.find((a) => a.questionId === question.questionId)?.answerText ?? ''}
                onChange={(e) => onChangeAnswer(question.questionId, e.target.value)}
              />
            </Flex>
          ))}
        </Flex>
      </Flex>
    </Card>
  );
};
