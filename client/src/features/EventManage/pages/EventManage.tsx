import { Flex } from '@/shared/components/Flex';

import { EventInfoSection } from '../components/EventInfoSection';
import { GuestManageSection } from '../components/GuestManageSection';
import { EventManageContainer } from '../containers/EventManageContainer';
import { useEventManage } from '../hooks/useEventManage';

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
