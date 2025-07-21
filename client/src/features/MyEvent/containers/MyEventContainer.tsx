import { ReactNode } from 'react';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { STATUS_MESSAGES } from '../constants';
import { useEvents } from '../hooks/useEvents';

type MyEventContainerProps = {
  children: ReactNode;
};

export const MyEventContainer = ({ children }: MyEventContainerProps) => {
  const { loading, error } = useEvents();

  if (loading) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center">
        <Text type="Body" weight="regular" color="#666">
          {STATUS_MESSAGES.LOADING_EVENTS}
        </Text>
      </Flex>
    );
  }

  if (error) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center">
        <Text type="Body" weight="regular" color="#ff4444">
          {error}
        </Text>
      </Flex>
    );
  }

  return <Flex dir="column">{children}</Flex>;
};
