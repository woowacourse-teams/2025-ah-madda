import { css } from '@emotion/react';

import { Card } from '../../../shared/components/Card';
import { Flex } from '../../../shared/components/Flex';
import { Icon } from '../../../shared/components/Icon';
import { Text } from '../../../shared/components/Text';
import type { EventDetail } from '../types/index';

type LocationCardProps = Pick<EventDetail, 'location'>;

export const LocationCard = ({ location }: LocationCardProps) => {
  return (
    <Flex
      dir="column"
      height="235px"
      css={css`
        flex: 1;
      `}
    >
      <Card
        css={css`
          height: 100%;
        `}
      >
        <Flex dir="column" gap="16px">
          <Flex gap="8px" alignItems="center">
            <Icon name="location" size={18} />
            <Text type="caption">장소</Text>
          </Flex>
          <Text type="caption">{location}</Text>
        </Flex>
      </Card>
    </Flex>
  );
};
