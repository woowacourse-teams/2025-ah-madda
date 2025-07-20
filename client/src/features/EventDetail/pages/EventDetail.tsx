import { Flex } from '../../../shared/components/Flex';
import { Icon } from '../../../shared/components/Icon';
import { Text } from '../../../shared/components/Text';
import { DescriptionCard } from '../components/DescriptionCard';
import { EventHeader } from '../components/EventHeader';
import { LocationCard } from '../components/LocationCard';
import { ParticipantsCard } from '../components/ParticipantsCard';
import { PreQuestionCard } from '../components/PreQuestionCard';
import { SubmitButtonCard } from '../components/SubmitButtonCard';
import { TimeInfoCard } from '../components/TimeInfoCard';

export const EventDetail = () => {
  return (
    <Flex
      dir="column"
      width="100%"
      css={{
        maxWidth: '784px',
        width: '100%',
        margin: '0 auto',
        padding: '28px 14px',
        gap: '24px',
        boxSizing: 'border-box',
      }}
    >
      <EventHeader />
      <Flex
        css={{
          display: 'flex',
          flexDirection: 'row',
          gap: '24px',
          width: '100%',
          '@media (max-width: 768px)': {
            flexDirection: 'column',
          },
        }}
      >
        <TimeInfoCard />
        <LocationCard />
      </Flex>

      <ParticipantsCard />
      <DescriptionCard />
      <PreQuestionCard />
      <SubmitButtonCard />
    </Flex>
  );
};
