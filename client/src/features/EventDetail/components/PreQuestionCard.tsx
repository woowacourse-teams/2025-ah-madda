import { Card } from '../../../shared/components/Card';
import { Flex } from '../../../shared/components/Flex';
import { Text } from '../../../shared/components/Text';

export const PreQuestionCard = () => (
  <Card>
    <Flex dir="column" gap="16px">
      <Text type="caption">사전 질문</Text>
      <Flex dir="column" gap="16px">
        <Flex dir="column" gap="8px">
          <Text type="caption" weight="bold">
            이 이벤트에 참여하는 이유를 간단히 알려주세요
          </Text>
          <Text type="caption" color="gray">
            참가 신청 시 작성할 수 있습니다.
          </Text>
        </Flex>
        <Flex dir="column" gap="8px">
          <Text type="caption" weight="bold">
            이벤트를 통해 기대하는 점이 있다면 알려주세요
          </Text>
          <Text type="caption" color="gray">
            참가 신청 시 작성할 수 있습니다.
          </Text>
        </Flex>
      </Flex>
    </Flex>
  </Card>
);
