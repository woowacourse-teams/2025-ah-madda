import { css } from '@emotion/react';

import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Icon } from '../../../../shared/components/Icon';
import { Text } from '../../../../shared/components/Text';
import type { EventDetail } from '../types/index';

type TimeInfoCardProps = Pick<EventDetail, 'deadlineTime' | 'startTime' | 'endTime'>;

export const TimeInfoCard = ({ deadlineTime, startTime, endTime }: TimeInfoCardProps) => {
  return (
    <Flex
      dir="column"
      css={css`
        flex: 1;
      `}
    >
      <Card>
        <Flex gap="8px" margin="0 0 16px 0" alignItems="center">
          <Icon name="clock" size={18} />
          <Text type="caption">시간 정보</Text>
        </Flex>
        <Flex dir="column" gap="4px">
          <Text type="caption" color="gray">
            신청 마감
          </Text>
          <Text type="caption" color="red">
            {deadlineTime}
          </Text>
          <Text type="caption" color="gray">
            이벤트 시작
          </Text>
          <Text type="caption">{startTime}</Text>
          <Text type="caption" color="gray">
            이벤트 종료
          </Text>
          <Text type="caption">{endTime}</Text>
        </Flex>
      </Card>
    </Flex>
  );
};
