import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { theme } from '@/shared/styles/theme';

export const ActionButtons = () => {
  const navigate = useNavigate();

  return (
    <>
      <DesktopButtonContainer>
        <Button size="md" iconName="share" onClick={() => navigate('/event/new')}>
          조직 초대
        </Button>
        <Button size="md" iconName="plus" onClick={() => navigate('/event/new')}>
          이벤트 생성
        </Button>
      </DesktopButtonContainer>

      <MobileFixedCTA>
        <Button size="md" iconName="share" variant="outline" onClick={() => navigate('/event/new')}>
          조직 초대
        </Button>
        <Button size="md" iconName="plus" onClick={() => navigate('/event/new')}>
          이벤트 생성
        </Button>
      </MobileFixedCTA>
    </>
  );
};

const DesktopButtonContainer = styled(Flex)`
  justify-content: flex-end;
  align-items: center;
  gap: 10px;

  @media (max-width: 768px) {
    display: none;
  }
`;

const MobileFixedCTA = styled.div`
  display: none;

  @media (max-width: 768px) {
    display: flex;
    position: fixed;
    bottom: 20px;
    left: 0;
    right: 0;
    z-index: 1000;
    padding: 0 20px;

    gap: 12px;

    > button {
      flex: 1;
    }
  }
`;
