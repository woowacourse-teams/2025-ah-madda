import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';

import { EventCard } from '../components/EventCard';
import { UI_LABELS, STATUS_MESSAGES, TAB_VALUES } from '../constants';
import { MyEventContainer } from '../containers/MyEventContainer';
import { useEvents } from '../hooks/useEvents';

export const MyEvent = () => {
  const { events } = useEvents();

  return (
    <PageLayout
      header={
        <Header
          left={
            <Flex alignItems="center" gap="12px">
              <IconButton name="back" size={14} />
              <Text as="h1" type="Title" weight="bold">
                {UI_LABELS.PAGE_TITLE}
              </Text>
            </Flex>
          }
        />
      }
    >
      <MyEventContainer>
        <Tabs defaultValue={TAB_VALUES.HOST}>
          <Flex width="392px" css={{ marginTop: '20px' }}>
            <Tabs.List css={{ width: '100%' }}>
              <Tabs.Trigger value={TAB_VALUES.HOST}>{UI_LABELS.HOST_TAB}</Tabs.Trigger>
              <Tabs.Trigger value={TAB_VALUES.PARTICIPATE}>
                {UI_LABELS.PARTICIPATE_TAB}
              </Tabs.Trigger>
            </Tabs.List>
          </Flex>

          <Tabs.Content value={TAB_VALUES.HOST} css={{ marginTop: '37.5px' }}>
            <Flex dir="column" gap="16px">
              <Flex alignItems="center" gap="8px">
                <Icon name="calendar" size={21} color="#0A0A0A" />
                <Text type="Body" weight="bold" color="black">
                  {UI_LABELS.ONGOING_HOST_EVENTS}
                </Text>
              </Flex>

              {events.hostEvents.length > 0 ? (
                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))',
                    gap: '24px',
                  }}
                >
                  {events.hostEvents.map((event) => (
                    <EventCard key={event.id} {...event} />
                  ))}
                </div>
              ) : (
                <Text type="Body" weight="regular" color="#666">
                  {STATUS_MESSAGES.NO_HOST_EVENTS}
                </Text>
              )}
            </Flex>
          </Tabs.Content>

          <Tabs.Content value={TAB_VALUES.PARTICIPATE} css={{ marginTop: '37.5px' }}>
            <Flex dir="column" gap="16px">
              <Flex alignItems="center" gap="8px">
                <Icon name="calendar" size={21} color="#0A0A0A" />
                <Text type="Body" weight="bold" color="black">
                  {UI_LABELS.PARTICIPATING_EVENTS}
                </Text>
              </Flex>

              {events.participateEvents.length > 0 ? (
                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
                    gap: '24px',
                  }}
                >
                  {events.participateEvents.map((event) => (
                    <EventCard key={event.id} {...event} />
                  ))}
                </div>
              ) : (
                <Text type="Body" weight="regular" color="#666">
                  {STATUS_MESSAGES.NO_PARTICIPATE_EVENTS}
                </Text>
              )}
            </Flex>
          </Tabs.Content>
        </Tabs>
      </MyEventContainer>
    </PageLayout>
  );
};
