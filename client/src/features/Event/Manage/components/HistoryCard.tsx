import styled from '@emotion/styled';

import { NotifyHistoryAPIResponse } from '@/api/types/event';
import { Badge } from '@/shared/components/Badge';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';
import { formatDate } from '@/shared/utils/dateUtils';

export const HistoryCard = ({ recipientCount, content, sentAt }: NotifyHistoryAPIResponse) => {
  return (
    <StyledHistoryCard
      dir="column"
      justifyContent="space-between"
      alignItems="flex-start"
      gap="4px"
    >
      <Flex width="100%" justifyContent="space-between" alignItems="center">
        <Text type="Body" weight="semibold">
          {content.length > 20 ? `${content.slice(0, 20)}...` : content}
        </Text>
        <Badge variant="blue">{recipientCount}명</Badge>
      </Flex>
      <Text type="Label" color={theme.colors.gray500}>
        {formatDate({
          start: sentAt,
          options: { pattern: 'YYYY년 MM월 DD일 E A HH:mm', dayOfWeekFormat: 'long' },
        })}
      </Text>
    </StyledHistoryCard>
  );
};

const StyledHistoryCard = styled(Flex)`
  padding: 12px 8px;
  border-bottom: 1px solid ${theme.colors.gray200};
`;
