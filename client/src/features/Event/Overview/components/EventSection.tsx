import { ReactNode } from 'react';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

type EventSectionProps = {
  title: string;
  children: ReactNode;
};

export const EventSection = ({ title, children }: EventSectionProps) => {
  return (
    <Flex as="section" dir="column" gap="16px" width="100%">
      <Flex
        alignItems="center"
        gap="4px"
        aria-label={`${title}이 마감일인 이벤트 목록입니다.`}
        tabIndex={0}
      >
        <Text as="h2" type="Heading" weight="bold" aria-hidden="true">
          {title}
        </Text>
      </Flex>
      {children}
    </Flex>
  );
};
