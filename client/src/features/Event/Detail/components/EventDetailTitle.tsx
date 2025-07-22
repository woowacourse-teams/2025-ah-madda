import { css } from '@emotion/react';

import { Flex } from '../../../../shared/components/Flex';
import { Icon } from '../../../../shared/components/Icon';
import { Text } from '../../../../shared/components/Text';
import type { EventDetail } from '../types/index';

type EventHeaderProps = Pick<EventDetail, 'title' | 'author'>;

export const EventDetailTitle = ({ title, author }: EventHeaderProps) => {
  return (
    <Flex dir="column" gap="12px">
      <Text type="Title" weight="bold">
        {title}
      </Text>
      <Flex alignItems="center" gap="4px">
        <Icon name="users" size={18} />
        <Text type="caption">{`주최: ${author}`}</Text>
      </Flex>
    </Flex>
  );
};
