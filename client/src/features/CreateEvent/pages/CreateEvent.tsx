import { Flex } from '../../../shared/components/Flex';
import { Header } from '../../../shared/components/Header';
import { IconButton } from '../../../shared/components/IconButton';
import { Text } from '../../../shared/components/Text';
import { EventCreateForm } from '../components/EventCreateForm';

export const CreateEvent = () => {
  return (
    <>
      <Header
        left={
          <Flex alignItems="center" gap="12px">
            <IconButton name="back" size={14} />
            <Text type="caption">돌아가기</Text>
          </Flex>
        }
        css={{
          backgroundColor: 'white',
        }}
      />
      <Flex
        dir="column"
        css={{
          width: '100%',
          maxWidth: '784px',
          margin: '0 auto',
          padding: '28px 14px',
          gap: '24px',
          boxSizing: 'border-box',
        }}
      >
        <EventCreateForm />
      </Flex>
    </>
  );
};
