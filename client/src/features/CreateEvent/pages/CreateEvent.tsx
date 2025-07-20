import { Flex } from '../../../shared/components/Flex';
import { EventCreateForm } from '../components/EventCreateForm';

export const CreateEvent = () => {
  return (
    <Flex
      dir="column"
      css={{
        width: '100%',
        maxWidth: '784px',
        margin: '0 auto',
        padding: '28px 14px',
        gap: '24px',
      }}
    >
      <EventCreateForm />
    </Flex>
  );
};
