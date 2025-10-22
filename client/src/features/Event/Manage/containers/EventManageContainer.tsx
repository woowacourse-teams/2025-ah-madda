import { Flex } from '@/shared/components/Flex';

type EventManageContainerProps = {
  children: React.ReactNode;
};

export const EventManageContainer = ({ children }: EventManageContainerProps) => {
  return (
    <Flex dir="column" gap="24px" margin="60px 0 0 0" padding="30px 20px 0 20px">
      {children}
    </Flex>
  );
};
