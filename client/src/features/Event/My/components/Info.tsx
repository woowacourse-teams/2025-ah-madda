import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

export const Info = () => {
  return (
    <Flex
      dir="column"
      justifyContent="flex-start"
      margin="60px 0 30px 0"
      padding="20px 0"
      gap="10px"
    >
      <Text as="h1" type="Display" weight="bold" color="gray900">
        내 이벤트
      </Text>
      <Text as="h2" type="Body" weight="medium" color="gray900">
        내가 주최한 이벤트와 참여한 이벤트를 볼 수 있습니다.
      </Text>
    </Flex>
  );
};
