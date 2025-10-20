import styled from '@emotion/styled';
import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';

export const ActionButtons = () => {
  const navigate = useNavigate();
  const { organizationId } = useParams();

  return (
    <>
      <DesktopButtonContainer>
        <Button size="md" iconName="plus" onClick={() => navigate(`/${organizationId}/event/new`)}>
          이벤트 생성
        </Button>
      </DesktopButtonContainer>

      <MobileFixedCTA>
        <Button size="md" iconName="plus" onClick={() => navigate(`/${organizationId}/event/new`)}>
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
  padding: 0 20px;

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
