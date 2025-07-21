import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { useEventManage } from '../hooks/useEventManage';

type EventManageContainerProps = {
  children: React.ReactNode;
};

export const EventManageContainer = ({ children }: EventManageContainerProps) => {
  const { loading, error } = useEventManage();

  if (loading) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center" css={{ minHeight: '100vh' }}>
        <Text type="Body" weight="regular" color="#666">
          이벤트 데이터를 불러오는 중...
        </Text>
      </Flex>
    );
  }

  if (error) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center" css={{ minHeight: '100vh' }}>
        <Text type="Body" weight="regular" color="#ff4444">
          {error}
        </Text>
      </Flex>
    );
  }
  return <Flex dir="column">{children}</Flex>;
};
