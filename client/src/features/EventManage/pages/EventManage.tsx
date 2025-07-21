import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';

import { EventInfoSection } from '../components/EventInfoSection';
import { GuestManageSection } from '../components/GuestManageSection';
import { EventManageContainer } from '../containers/EventManageContainer';
import { useEventManage } from '../hooks/useEventManage';

export const EventManage = () => {
  const { data } = useEventManage();

  return (
    <PageLayout
      header={
        <Header
          left={
            <Flex alignItems="center" gap="12px">
              <IconButton name="back" size={14} />
              <Text as="h1" type="Title" weight="semibold">
                이벤트 관리
              </Text>
            </Flex>
          }
        />
      }
    >
      <EventManageContainer>
        <Flex as="main" gap="40px" css={{ marginTop: '32px' }} width="100%">
          <EventInfoSection eventInfo={data.eventInfo} />
          <GuestManageSection
            completedGuests={data.completedGuests}
            pendingGuests={data.pendingGuests}
          />
        </Flex>
      </EventManageContainer>
    </PageLayout>
  );
};
