import { Flex } from '@/shared/components/Flex';

import { EventInfoSection, GuestManageSection } from '../components';
import { EventManageContainer } from '../containers/EventManageContainer';
import { useEventManage } from '../hooks';

export const EventManage = () => {
  const { data } = useEventManage();

  return (
    <EventManageContainer>
      <Flex as="main" gap="40px" css={{ marginTop: '32px' }}>
        <EventInfoSection eventInfo={data.eventInfo} />
        <GuestManageSection
          completedGuests={data.completedGuests}
          pendingGuests={data.pendingGuests}
        />
      </Flex>
    </EventManageContainer>
  );
};
