import { css, keyframes } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import Ahmadda from '@/assets/icon/ahmadda.webp';
import Point from '@/assets/icon/point.webp';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

export const Info = () => {
  const navigate = useNavigate();

  return (
    <Flex
      dir="column"
      justifyContent="center"
      alignItems="center"
      width="100%"
      padding="60px 20px 0"
    >
      <Flex
        dir="row"
        justifyContent="space-around"
        alignItems="center"
        gap="30px"
        width="100%"
        padding="60px 0"
        css={css`
          @media (max-width: 768px) {
            flex-direction: column-reverse;
            padding: 40px 0px;
          }
        `}
      >
        <Flex dir="column" alignItems="flex-start">
          <Text type="caption">
            슬랙이나 메신저에서 참여하고 싶었던 이벤트를 놓친 경험 있으신가요?
          </Text>
          <Text type="caption">혹은 이벤트를 열었는데 반응이 없어 답답하거나,</Text>
          <Text type="caption">리마인더 메세지를 여러번 보내야 했던 적 있으신가요?</Text>
        </Flex>
        <Flex
          justifyContent="center"
          alignItems="center"
          css={css`
            position: relative;
          `}
        >
          <Logo src={Ahmadda} alt="아마다 로고" />
          <PointIcon src={Point} alt="Point" className="point1" />
          <PointIcon src={Point} alt="Point" className="point2" />
          <PointIcon src={Point} alt="Point" className="point3" />
        </Flex>
      </Flex>
      <Button width="100%" size="lg" onClick={() => navigate('/event')}>
        이벤트 보러가기
      </Button>
    </Flex>
  );
};

const Logo = styled.img`
  width: 150px;
  height: 220px;
`;

const pop = keyframes`
  0% {
    opacity: 0;
    transform: scale(0.5);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }

  100% {
    opacity: 1;
    transform: scale(1);
  }
`;

const PointIcon = styled.img`
  position: absolute;
  width: 40px;
  height: 100px;
  opacity: 0;
  animation: ${pop} 0.6s ease-out forwards;

  &.point1 {
    top: 30px;
    right: 0px;
    animation-delay: 0s;
  }

  &.point2 {
    top: 20px;
    right: -30px;
    animation-delay: 0.6s;
  }

  &.point3 {
    top: 35px;
    right: -60px;
    animation-delay: 1.2s;
  }
`;
