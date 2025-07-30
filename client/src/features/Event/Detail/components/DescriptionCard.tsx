import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Text } from '../../../../shared/components/Text';
import { EventDetail } from '../../types/Event';

type DescriptionCardProps = Pick<EventDetail, 'description'>;

export const DescriptionCard = ({ description }: DescriptionCardProps) => {
  return (
    <Card>
      <Flex dir="column" gap="8px">
        <Text type="Label">이벤트 소개</Text>
        <Text type="Label">{description}</Text>
      </Flex>
    </Card>
  );
};
