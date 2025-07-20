import { Flex } from '../../../shared/components/Flex';
import { Icon } from '../../../shared/components/Icon';
import { Text } from '../../../shared/components/Text';

export const EventHeader = () => (
  <Flex dir="column" gap="12px">
    <Text type="Title" weight="bold" css={{ textAlign: 'left' }}>
      솔라의 UI/UX 특강 @solar
    </Text>
    <Flex alignItems="center" gap="4px">
      <Icon name="users" size={18} />
      <Text type="caption">주최: 솔라</Text>
    </Flex>
  </Flex>
);
