import { Card } from '../../../shared/components/Card';
import { Flex } from '../../../shared/components/Flex';
import { Text } from '../../../shared/components/Text';

export const DescriptionCard = () => (
  <Card>
    <Flex dir="column" gap="8px">
      <Text type="caption">이벤트 소개</Text>
      <Text type="caption">UX/UI에 관심있는 사람들을 위한 특강</Text>
    </Flex>
  </Card>
);
