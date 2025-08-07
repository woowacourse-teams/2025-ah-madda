import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Tabs } from '@/shared/components/Tabs';

import { EventInfoSection } from '../components/EventInfoSection';
import { GuestManageSection } from '../components/GuestManageSection';
import { EventManageContainer } from '../containers/EventManageContainer';

export const EventManagePage = () => {
  const navigate = useNavigate();

  return (
    <PageLayout
      header={
        <Header
          left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />}
          right={
            <Button size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          }
        />
      }
    >
      <EventManageContainer>
        <Tabs defaultValue="detail">
          <Tabs.List
            css={css`
              width: 40%;
              @media (max-width: 768px) {
                width: 100%;
              }
            `}
          >
            <Tabs.Trigger value="detail">이벤트 정보</Tabs.Trigger>
            <Tabs.Trigger value="applications">신청 현황</Tabs.Trigger>
          </Tabs.List>

          <Tabs.Content value="detail">
            <EventInfoSection />
          </Tabs.Content>

          <Tabs.Content value="applications">
            <GuestManageSection />
          </Tabs.Content>
        </Tabs>
      </EventManageContainer>
    </PageLayout>
  );
};
