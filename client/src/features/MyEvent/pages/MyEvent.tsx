import { Flex } from '../../../shared/components/Flex';
import { Header } from '../../../shared/components/Header';
import { Icon } from '../../../shared/components/Icon';
import { IconButton } from '../../../shared/components/IconButton';
import { Tabs } from '../../../shared/components/Tabs';
import { Text } from '../../../shared/components/Text';
import { EventCard } from '../components/EventCard';
import { useEvents } from '../hooks/useEvents';

export const MyEvent = () => {
  const { events, loading, error } = useEvents();

  if (loading) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center">
        <Text type="Body" weight="regular" color="#666">
          이벤트를 불러오는 중...
        </Text>
      </Flex>
    );
  }

  if (error) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center">
        <Text type="Body" weight="regular" color="#ff4444">
          {error}
        </Text>
      </Flex>
    );
  }

  return (
    <Flex dir="column">
      <Header
        left={
          <Flex alignItems="center" gap="12px">
            <IconButton name="back" size={14} />
            <Text as="h1" type="Title" weight="bold">
              내 이벤트
            </Text>
          </Flex>
        }
      />

      <Tabs defaultValue="host">
        <Flex width="392px" css={{ marginTop: '20px' }}>
          <Tabs.List css={{ width: '100%' }}>
            <Tabs.Trigger value="host">주최 이벤트</Tabs.Trigger>
            <Tabs.Trigger value="participate">참여 이벤트</Tabs.Trigger>
          </Tabs.List>
        </Flex>

        <Tabs.Content value="host" css={{ marginTop: '37.5px' }}>
          <Flex dir="column" gap="16px">
            <Flex alignItems="center" gap="8px">
              <Icon name="calendar" size={21} color="#0A0A0A" />
              <Text type="Body" weight="bold" color="black">
                진행 중인 이벤트
              </Text>
            </Flex>

            {events.hostEvents.length > 0 ? (
              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
                  gap: '16px',
                }}
              >
                {events.hostEvents.map((event) => (
                  <EventCard key={event.id} event={event} />
                ))}
              </div>
            ) : (
              <Text type="Body" weight="regular" color="#666">
                주최한 이벤트가 없습니다.
              </Text>
            )}
          </Flex>
        </Tabs.Content>

        <Tabs.Content value="participate" css={{ marginTop: '37.5px' }}>
          <Flex dir="column" gap="16px">
            <Flex alignItems="center" gap="8px">
              <Icon name="calendar" size={21} color="#0A0A0A" />
              <Text type="Body" weight="bold" color="black">
                참여 중인 이벤트
              </Text>
            </Flex>

            {events.participateEvents.length > 0 ? (
              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
                  gap: '16px',
                }}
              >
                {events.participateEvents.map((event) => (
                  <EventCard key={event.id} event={event} />
                ))}
              </div>
            ) : (
              <Text type="Body" weight="regular" color="#666">
                참여한 이벤트가 없습니다.
              </Text>
            )}
          </Flex>
        </Tabs.Content>
      </Tabs>
    </Flex>
  );
};
