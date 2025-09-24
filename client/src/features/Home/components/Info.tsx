import { css, keyframes } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import Ahmadda from '@/assets/icon/ahmadda.webp';
import Point from '@/assets/icon/point.webp';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';

export const Info = () => {
  const { error } = useToast();
  const navigate = useNavigate();

  const handleEnterOrganizationSpace = () => {
    if (!isAuthenticated()) {
      error(`로그인이 필요한 서비스입니다.\n먼저 로그인해 주세요.`, {
        duration: 3000,
      });
      return;
    }
    navigate(`/organization`);
  };

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
          padding="60px 10px"
          css={css`
            @media (max-width: 768px) {
              flex-direction: column-reverse;
              padding: 40px 0px;
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
            <PointIcon src={Point} alt="Point" className="point3" width={40} height={100} />
          </Flex>
        </Flex>
        <Button size="full" onClick={handleEnterOrganizationSpace}>
          이벤트 스페이스 보러가기
        </Button>
      </Flex>
    </>
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
