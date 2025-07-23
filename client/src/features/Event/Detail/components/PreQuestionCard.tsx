import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Input } from '../../../../shared/components/Input';
import { Text } from '../../../../shared/components/Text';
import type { EventDetail } from '../../../Event/types/Event';

type PreQuestionCardProps = Pick<EventDetail, 'questions'>;

export const PreQuestionCard = ({ questions }: PreQuestionCardProps) => {
  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Text type="caption">사전 질문</Text>

        <Flex dir="column" gap="24px">
          {questions.map((question) => (
            <Flex key={question.questionId} dir="column" gap="4px">
              <label htmlFor={`question-${question.questionId}`}>
                <Text type="caption" weight="bold">
                  {question.questionText}
                  {question.isRequired && <span style={{ color: 'red' }}> *</span>}
                </Text>
              </label>
              <Input
                id={`question-${question.questionId}`}
                label=""
                placeholder="답변을 입력하세요"
              />
            </Flex>
          ))}
        </Flex>
      </Flex>
    </Card>
  );
};
