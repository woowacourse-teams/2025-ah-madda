import { css, keyframes } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { organizationQueryOptions } from '@/api/queries/organization';
import Ahmadda from '@/assets/icon/ahmadda.webp';
import Point from '@/assets/icon/point.webp';
import { NicknameModal } from '@/features/Event/Overview/components/NicknameModal';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';

export const Info = () => {
  const { isOpen, open, close } = useModal();
  const navigate = useNavigate();

  //E.TODO 추후 organizationId 받아오기
  const { data: profileData } = useQuery(organizationQueryOptions.profile(1));

  const handleButtonClick = () => {
    if (profileData?.nickname) {
      navigate('/event');
      return;
    }

    if (!isAuthenticated()) {
      alert('로그인이 필요한 서비스입니다. 먼저 로그인해 주세요.');
      return;
    }

    open();
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
        <Button width="100%" size="lg" onClick={handleButtonClick}>
          이벤트 보러가기
        </Button>
      </Flex>
      <NicknameModal isOpen={isOpen} onClose={close} />
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
