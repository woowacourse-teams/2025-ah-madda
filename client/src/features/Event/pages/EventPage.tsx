import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const EventPage = () => {
  return (
    <PageLayout
      header={
        <Header
          left="아맞다"
          right={
            <Flex gap="8px">
              <Button width="80px" size="sm" variant="outlined" fontColor="#2563EB">
                로그아웃
              </Button>
              <Button width="80px" size="sm">
                내 이벤트
              </Button>
            </Flex>
          }
        />
      }
    >
      <OrganizationInfo />
      <EventList />
    </PageLayout>
  );
};
