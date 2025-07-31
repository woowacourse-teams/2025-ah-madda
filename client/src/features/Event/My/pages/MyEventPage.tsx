import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';

import { EventTabs } from '../components/EventTabs';
import { MyEventContainer } from '../containers/MyEventContainer';

export const MyEventPage = () => {
  const navigate = useNavigate();

  return (
    <PageLayout
      header={
        <Header
          left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />}
          right={
            <Button width="80px" size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          }
        />
      }
    >
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
