import { Card } from '../../../shared/components/Card';
import { Flex } from '../../../shared/components/Flex';
import { Text } from '../../../shared/components/Text';
import type { EventDetail } from '../types/index';

type DescriptionCardProps = Pick<EventDetail, 'description'>;

export const DescriptionCard = ({ description }: DescriptionCardProps) => {
  return (
    <Card>
      <Flex dir="column" gap="8px">
        <Text type="caption">이벤트 소개</Text>
        <Text type="caption">{description}</Text>
      </Flex>
    </Card>
  );
};
