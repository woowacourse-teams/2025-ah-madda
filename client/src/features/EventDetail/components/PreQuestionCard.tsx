import { Card } from '../../../shared/components/Card';
import { Flex } from '../../../shared/components/Flex';
import { Text } from '../../../shared/components/Text';
import type { EventDetail } from '../types/index';

type PreQuestionCardProps = Pick<EventDetail, 'preQuestions'>;

export const PreQuestionCard = ({ preQuestions }: PreQuestionCardProps) => (
  <Card>
    <Flex dir="column" gap="16px">
      <Text type="caption">사전 질문</Text>
      <Flex dir="column" gap="16px">
        {preQuestions.map((question, index) => (
          <Flex key={index} dir="column" gap="8px">
            <Text type="caption" weight="bold">
              {question}
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
