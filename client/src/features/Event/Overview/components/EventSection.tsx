import { ReactNode } from 'react';

import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';

type EventSectionProps = {
  title: string;
  children: ReactNode;
};

export const EventSection = ({ title, children }: EventSectionProps) => {
  return (
    <Flex as="section" dir="column" gap="16px" width="100%">
      <Flex alignItems="center" gap="4px">
        <Icon name="calendar" />
        <Text type="Body" weight="semibold">
          {title}
        </Text>
      </Flex>
      {children}
    </Flex>
  );
};
