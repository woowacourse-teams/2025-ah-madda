import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useSuspenseQueries } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { Flex } from '@/shared/components/Flex';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import Alarm2 from '../../../../../assets/icon/alarm-2.png';

import { GuestList } from './GuestList';

export const AttendanceOverview = ({ eventId }: { eventId: number }) => {
  const [{ data: guests = [] }, { data: nonGuests = [] }] = useSuspenseQueries({
    queries: [eventQueryOptions.guests(eventId), eventQueryOptions.nonGuests(eventId)],
  });

  return (
    <Flex as="section" width="100%" dir="column">
      <GuestList
        eventId={eventId}
        title={`신청 완료 (${guests.length}명)`}
        titleColor={theme.colors.primary600}
        guests={guests}
      />
      <GuestList
        eventId={eventId}
        title={`미신청 (${nonGuests.length}명)`}
        titleColor={theme.colors.red600}
        guests={nonGuests}
      />
      <Flex padding="40px 16px 20px">
        <Text type="Body" weight="medium" color={theme.colors.gray600}>
          이름을 클릭하면, 알림을 보낼 수 있어요.
        </Text>
      </Flex>

      <Spacing
        height="1px"
        css={css`
          border: 1px solid ${theme.colors.gray200};
        `}
      />

      <Flex dir="column" gap="16px" alignItems="flex-start" padding="20px">
        <Text type="Heading" weight="bold" color={theme.colors.gray900}>
          알림을 받지 않는 조직원이라고 떠요!
        </Text>
        <Text type="Body" weight="medium" color={theme.colors.gray700}>
          오른쪽의 앱 다운로드를 통해 알림을 받을 수 있어요. 친구와 함께 앱을 설치하고, 알림을
          주고받아 보세요!
        </Text>
        <Img src={Alarm2} alt="alarm" width="800" />
      </Flex>
    </Flex>
  );
};

const Img = styled.img`
  width: 800px;
  border-radius: 12px;
`;
