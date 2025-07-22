import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Wowaw from '@/assets/icon/wowaw.png';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

export const OrganizationInfo = () => {
  return (
    <Flex
      dir="column"
      width="100%"
      gap="20px"
      css={css`
        max-width: 1120px;
        margin: 0 auto;
      `}
    >
      <Flex padding="20px 10px" justifyContent="space-between" alignItems="center" width="100%">
        <Flex dir="column" gap="8px">
          <Text type="Head" weight="bold">
            우아한테크코스 7기
          </Text>
          <Text type="Body">우아한테크코스의 이벤트를 모두 받아보세요.</Text>
          <Text type="caption">6개의 이벤트가 열려있어요!</Text>
        </Flex>
        <Img src={Wowaw} />
      </Flex>
    </Flex>
  );
};

const Img = styled.img`
  width: 100%;
  max-width: clamp(160px, 30vw, 252px);
  height: auto;
  margin-right: 20px;
  padding: 20px 0;
`;
