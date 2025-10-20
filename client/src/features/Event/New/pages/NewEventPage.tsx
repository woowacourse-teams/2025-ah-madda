import { useParams } from 'react-router-dom';

import { Flex } from '@/shared/components/Flex';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventCreateForm } from '../components/EventCreateForm';

export const NewEventPage = () => {
  const { eventId } = useParams();
  const isEdit = !!eventId;

  return (
    <PageLayout>
      <Flex dir="column" width="100%" margin="0 auto" padding="28px 20px" gap="24px">
        <EventCreateForm isEdit={isEdit} eventId={eventId ? Number(eventId) : undefined} />
      </Flex>
    </PageLayout>
  );
};
