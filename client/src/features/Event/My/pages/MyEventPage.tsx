import { Flex } from '@/shared/components/Flex';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';

import { EventTabs } from '../components/EventTabs';
import { MyEventHeader } from '../components/MyEventHeader';
import { MyEventContainer } from '../containers/MyEventContainer';

export const MyEventPage = () => {
  return (
    <PageLayout header={<MyEventHeader />}>
      <MyEventContainer>
        <Flex justifyContent="flex-start" margin="30px 0 30px 0">
          <Text as="h2" type="Body" weight="medium" color="gray900">
            내가 주최한 이벤트와 참여한 이벤트를 볼 수 있습니다.
          </Text>
        </Flex>
        <EventTabs />
      </MyEventContainer>
    </PageLayout>
  );
};
