import { css } from '@emotion/react';
import { useParams } from 'react-router-dom';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

export const Info = () => {
  return (
    <Flex
      dir="row"
      justifyContent="space-between"
      alignItems="center"
      margin="60px 0 30px 0"
      padding="20px 0"
      gap="16px"
      width="100%"
    >
      <Flex dir="column" gap="8px">
        <Text as="h1" type="Display" weight="bold" color="gray900">
          마이 페이지
        </Text>
        <Text as="h2" type="Body" weight="medium" color="gray700">
          내가 주최하고, 참여한 이벤트를 확인해보세요.
        </Text>
      </Flex>
    </Flex>
  );
};
