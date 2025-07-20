import { Card } from '../../../shared/components/Card';
import { Flex } from '../../../shared/components/Flex';
import { Icon } from '../../../shared/components/Icon';
import { Text } from '../../../shared/components/Text';

export const TimeInfoCard = () => (
  <Flex dir="column" css={{ flex: 1, minHeight: '235px' }}>
    <Card>
      <Flex gap="8px" css={{ marginBottom: '16px' }}>
        <Icon name="clock" size={18} />
        <Text type="caption">시간 정보</Text>
      </Flex>
      <Flex dir="column" gap="4px">
        <Text type="caption" color="gray">
          신청 마감
        </Text>
        <Text type="caption" color="red">
          2025년 7월 14일 월요일 오후 03:00
        </Text>
        <Text type="caption" color="gray">
          이벤트 시작
        </Text>
        <Text type="caption">2025년 7월 14일 월요일 오후 05:00</Text>
        <Text type="caption" color="gray">
          이벤트 종료
        </Text>
        <Text type="caption">2025년 7월 14일 월요일 오후 10:00</Text>
      </Flex>
    </Card>
  </Flex>
);
