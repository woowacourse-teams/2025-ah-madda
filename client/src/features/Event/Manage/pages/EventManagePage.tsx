import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useCloseEventRegistration } from '@/api/mutations/useCloseEventRegistration';
import { eventQueryOptions } from '@/api/queries/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Tabs } from '@/shared/components/Tabs';

import { EventInfoSection } from '../components/EventInfoSection';
import { GuestManageSection } from '../components/GuestManageSection';
import { EventManageContainer } from '../containers/EventManageContainer';

export const EventManagePage = () => {
  const navigate = useNavigate();
  const { eventId: eventIdParam } = useParams();
  const eventId = Number(eventIdParam);
  const { data: event, refetch } = useQuery(eventQueryOptions.detail(eventId));
  const { mutate: closeEventRegistration } = useCloseEventRegistration();

  const isClosed = event?.registrationEnd ? new Date(event.registrationEnd) < new Date() : false;

  const handleButtonClick = () => {
    if (confirm('이벤트를 마감하시겠습니까?')) {
      closeEventRegistration(eventId, {
        onSuccess: () => {
          alert('이벤트가 마감되었습니다.');
          refetch();
        },
        onError: (error) => {
          if (error instanceof HttpError) {
            alert(error.message);
          }
        },
      });
    }
  };

  if (!event) return null;

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
          <DesktopButtonContainer justifyContent="flex-end">
            {isClosed ? (
              <Button size="sm" color="tertiary" variant="solid" disabled>
                마감됨
              </Button>
            ) : (
              <Button size="sm" color="tertiary" variant="solid" onClick={handleButtonClick}>
                마감하기
              </Button>
            )}
          </DesktopButtonContainer>

          <Tabs.Content value="detail">
            <EventInfoSection event={event} />
          </Tabs.Content>

          <Tabs.Content value="applications">
            <GuestManageSection />
          </Tabs.Content>
        </Tabs>

        <MobileFixedCTA>
          {isClosed ? (
            <Button size="md" color="tertiary" variant="solid" disabled>
              마감됨
            </Button>
          ) : (
            <Button size="md" color="tertiary" variant="solid" onClick={handleButtonClick}>
              마감하기
            </Button>
          )}
        </MobileFixedCTA>
      </EventManageContainer>
    </PageLayout>
  );
};

const DesktopButtonContainer = styled(Flex)`
  @media (max-width: 768px) {
    display: none;
  }
`;

const MobileFixedCTA = styled.div`
  display: none;

  @media (max-width: 768px) {
    display: flex;
    position: fixed;
    bottom: 20px;
    left: 0;
    right: 0;
    z-index: 1000;
    padding: 0 20px;

    > button {
      width: 100%;
    }
  }
`;
