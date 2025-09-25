import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';

import { Event } from '../../types/Event';
import { EventContainer } from '../containers/EventContainer';

import { CurrentEventList } from './CurrentEventList';
import { PastEventList } from './PastEventList';

type OverviewTabsProps = {
  currentEventData: Event[];
  pastEventData: Event[];
};

export const OverviewTabs = ({ currentEventData, pastEventData }: OverviewTabsProps) => {
  return (
    <EventContainer>
      <Tabs defaultValue="current">
        <Tabs.List>
          <Tabs.Trigger value="current">진행중인 이벤트</Tabs.Trigger>
          <Tabs.Trigger value="past">마감된 이벤트</Tabs.Trigger>
        </Tabs.List>

        <Tabs.Content value="current">
          <CurrentEventList events={currentEventData ?? []} />
        </Tabs.Content>

        <Tabs.Content value="past">
          {pastEventData.length === 0 ? (
            <Flex justifyContent="center" alignItems="center" height="200px">
              <Text type="Heading" weight="semibold" color="gray">
                등록된 이벤트가 없습니다.
              </Text>
            </Flex>
          ) : (
            <PastEventList events={pastEventData ?? []} />
          )}
        </Tabs.Content>
      </Tabs>
    </EventContainer>
  );
};
