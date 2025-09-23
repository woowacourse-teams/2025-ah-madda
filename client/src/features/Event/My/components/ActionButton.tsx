import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { IconButton } from '@/shared/components/IconButton';

type ActionButtonProps = {
  organizationId: number;
};
export const ActionButton = ({ organizationId }: ActionButtonProps) => {
  const navigate = useNavigate();
  const goCreateEvent = () => navigate(`/${organizationId}/event/new`);
  const goEditProfile = () => navigate(`/${organizationId}/profile`);

  return (
    <>
      <DesktopButtonContainer alignItems="center" gap="8px">
        <Button size="md" variant="outline" onClick={goCreateEvent}>
          + 이벤트 만들기
        </Button>

        <Button size="md" onClick={goEditProfile}>
          정보 수정
        </Button>
      </DesktopButtonContainer>

      <MobileButtonContainer alignItems="center" gap="8px">
        <IconButton name="plus" onClick={goCreateEvent} />
        <IconButton name="user" onClick={goEditProfile} />
      </MobileButtonContainer>
    </>
  );
};
const DesktopButtonContainer = styled(Flex)`
  @media (max-width: 768px) {
    display: none;
  }
`;

const MobileButtonContainer = styled(Flex)`
  display: none;

  @media (max-width: 768px) {
    display: flex;
  }
`;
