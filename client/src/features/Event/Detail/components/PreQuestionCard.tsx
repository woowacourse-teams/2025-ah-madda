import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Text } from '../../../../shared/components/Text';
import type { EventDetail } from '../../../Event/types/Event';

type PreQuestionCardProps = Pick<EventDetail, 'questions'>;

export const PreQuestionCard = ({ questions }: PreQuestionCardProps) => {
  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Text type="caption">사전 질문</Text>
        <Flex dir="column" gap="16px">
          {questions.map((question) => (
            <Flex key={question.questionId} dir="column" gap="8px">
              <Text type="caption" weight="bold">
                {question.questionText}
                {question.isRequired && <span style={{ color: 'red' }}> *</span>}
              </Text>
              <Text type="caption" color="gray">
                참가 신청 시 작성할 수 있습니다.
              </Text>
            </Flex>
          ))}
        </Flex>
      </Flex>
    </Card>
  );
};
