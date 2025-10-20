import { css, keyframes } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import Ahmadda from '@/assets/icon/ahmadda.webp';
import Point from '@/assets/icon/point.webp';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';

export const Info = () => {
  const navigate = useNavigate();
  const goOverview = () => navigate(`/organization`);
  return (
    <>
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
          padding="0 10px"
          css={css`
            @media (max-width: 768px) {
              flex-direction: column-reverse;
              padding: 0 0 30px 0;
              gap: 0px;
            }
          `}
        >
          <Flex
            dir="column"
            justifyContent="center"
            alignItems="flex-start"
            css={css`
              @media (max-width: 768px) {
                align-items: center;
              }
            `}
          >
            <Icon
              name="logo"
              css={css`
                width: 170px;
                height: 100px;

                @media (max-width: 768px) {
                  width: 130px;
                  height: 80px;
              `}
            />
            <Text
              as="h2"
              type="Heading"
              weight="medium"
              css={css`
                word-break: keep-all;
              `}
            >
              이벤트 스페이스 내 이벤트를 더 잘 참여하게!
            </Text>
          </Flex>
          <Flex
            justifyContent="center"
            alignItems="center"
            css={css`
              position: relative;
            `}
          >
            <Logo src={Ahmadda} alt="아마다 로고" width={150} height={220} />
            <PointIcon src={Point} alt="Point" className="point1" width={40} height={100} />
            <PointIcon src={Point} alt="Point" className="point2" width={40} height={100} />
          </Flex>
        </Flex>
        <Button size="full" onClick={goOverview}>
          이벤트 스페이스 보러가기
        </Button>
      </Flex>
    </>
  );
};

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

const Logo = styled.img`
  width: 200px;
  height: 300px;
  cursor: pointer;

  @media (max-width: 768px) {
    width: 180px;
    height: 250px;
  }
`;

const PointIcon = styled.img`
  position: absolute;
  width: 55px;
  height: 90px;
  opacity: 0;
  animation: ${pop} 0.6s ease-out forwards;

  &.point1 {
    top: 40px;
    right: 10px;
    animation-delay: 0s;
  }

  &.point2 {
    top: 30px;
    right: -20px;
    animation-delay: 0.6s;
  }

  @media (max-width: 768px) {
    width: 55px;
    height: 90px;

    &.point1 {
      top: 30px;
      right: 10px;
      animation-delay: 0s;
    }

    &.point2 {
      top: 20px;
      right: -20px;
      animation-delay: 0.6s;
  }
`;
