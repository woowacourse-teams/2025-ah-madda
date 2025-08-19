import { Flex } from '@/shared/components/Flex';

type EventManageContainerProps = {
  children: React.ReactNode;
};

export const EventManageContainer = ({ children }: EventManageContainerProps) => {
  return (
    <Flex dir="column" margin="24px" padding="60px 0 0 0">
      {children}
    </Flex>
  );
};
