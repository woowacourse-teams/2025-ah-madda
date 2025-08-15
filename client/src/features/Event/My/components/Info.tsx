import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

export const Info = () => {
  const navigate = useNavigate();

  return (
    <Flex
      dir="row"
      justifyContent="space-between"
      alignItems="center"
      margin="60px 0 30px 0"
      padding="20px 0"
      gap="16px"
      width="100%"
      css={css`
        @media (max-width: 768px) {
          flex-direction: column;
          align-items: flex-start;
          gap: 24px;
        }
      `}
    >
      <Flex dir="column" gap="8px">
        <Text as="h1" type="Display" weight="bold" color="gray900">
          내 이벤트
        </Text>
        <Text as="h2" type="Body" weight="medium" color="gray900">
          내가 주최하고, 참여한 이벤트를 확인해보세요.
        </Text>
      </Flex>
      <Button size="md" variant="outline" onClick={() => navigate('/event/new')}>
        + 이벤트 만들기
      </Button>
    </Flex>
  );
};
