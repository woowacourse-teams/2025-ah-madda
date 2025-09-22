import styled from '@emotion/styled';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { IconButton } from '@/shared/components/IconButton';

type EventActionButtonProps = {
  onEditEvent: VoidFunction;
  onShareEvent: VoidFunction;
};
export const EventActionButton = ({ onEditEvent, onShareEvent }: EventActionButtonProps) => {
  return (
    <>
      <DesktopButtonContainer alignItems="center" gap="8px">
        <Button color="secondary" onClick={onShareEvent}>
          공유하기
        </Button>
        <Button color="primary" onClick={onEditEvent}>
          수정
        </Button>
      </DesktopButtonContainer>

      <MobileButtonContainer alignItems="center" gap="8px">
        <IconButton name="share" onClick={onShareEvent} />
        <IconButton name="edit" onClick={onEditEvent} />
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
