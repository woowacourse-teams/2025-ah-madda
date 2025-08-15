import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';

import { myQueryOptions } from '@/api/queries/my';
import { Flex } from '@/shared/components/Flex';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';

import { groupEventsByDate } from '../../utils/groupEventsByDate';
import { STATUS_MESSAGES, TAB_VALUES } from '../constants';

import { EventSection } from './EventSection';
import { EventTabsList } from './EventTabsList';

export const EventTabs = () => {
  // E.TODO: organizationMemberId를 실제로 가져오는 로직 필요
  const organizationMemberId = 1; // 임시 값

  const { data: hostEvents = [] } = useQuery(myQueryOptions.event.hostEvents(organizationMemberId));

  const { data: participateEvents = [] } = useQuery(
    myQueryOptions.event.participateEvents(organizationMemberId)
  );

  const groupedHostEvents = groupEventsByDate(hostEvents);
  const groupedParticipateEvents = groupEventsByDate(participateEvents);

  return (
    <Tabs
      defaultValue={TAB_VALUES.HOST}
      css={css`
        width: 100%;
      `}
    >
      <EventTabsList />

      <Tabs.Content
        value={TAB_VALUES.HOST}
        css={css`
          margin-top: 37.5px;
        `}
      >
        {groupedHostEvents.length === 0 ? (
          <Flex justifyContent="center" alignItems="center" height="200px">
            <Text type="Heading" weight="semibold">
              {STATUS_MESSAGES.NO_HOST_EVENTS}
            </Text>
          </Flex>
        ) : (
          <Flex dir="column" width="100%" gap="20px">
            {groupedHostEvents.map(({ label, events }) => (
              <EventSection key={label} date={label} events={events} cardType={TAB_VALUES.HOST} />
            ))}
          </Flex>
        )}
      </Tabs.Content>

      <Tabs.Content
        value={TAB_VALUES.PARTICIPATE}
        css={css`
          margin-top: 37.5px;
        `}
      >
        {groupedParticipateEvents.length === 0 ? (
          <Flex justifyContent="center" alignItems="center" height="200px">
            <Text type="Heading" weight="semibold">
              {STATUS_MESSAGES.NO_PARTICIPATE_EVENTS}
            </Text>
          </Flex>
        ) : (
          <Flex dir="column" width="100%" gap="20px">
            {groupedParticipateEvents.map(({ label, events }) => (
              <EventSection
                key={label}
                date={label}
                events={events}
                cardType={TAB_VALUES.PARTICIPATE}
              />
            ))}
          </Flex>
        )}
      </Tabs.Content>
    </Tabs>
  );
};
