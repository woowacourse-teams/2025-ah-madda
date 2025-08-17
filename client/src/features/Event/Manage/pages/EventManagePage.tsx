import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useCloseEventRegistration } from '@/api/mutations/useCloseEventRegistration';
import { eventQueryOptions } from '@/api/queries/event';
import { profileQueryOptions } from '@/api/queries/profile';
import { Badge } from '@/shared/components/Badge';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { Spacing } from '@/shared/components/Spacing';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { formatDateTime } from '../../My/utils/date';
import { EventInfoSection } from '../components/EventInfoSection';
import { GuestManageSection } from '../components/GuestManageSection';
import { EventManageContainer } from '../containers/EventManageContainer';

export const EventManagePage = () => {
  const navigate = useNavigate();
  const { eventId: eventIdParam } = useParams();
  const eventId = Number(eventIdParam);
  const { data: event, refetch } = useQuery(eventQueryOptions.detail(eventId));
  const { data: profile } = useQuery(profileQueryOptions.profile());
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
          left={
            <Icon
              name="logo"
              size={55}
              onClick={() => navigate('/event')}
              css={css`
                cursor: pointer;
              `}
            />
          }
          right={
            <Button size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          }
        />
      }
    >
      <EventManageContainer>
        <Spacing height="56px" />
        <Flex dir="column" gap="12px">
          <Badge variant="blue">모집중</Badge>
          <Text
            type="Heading"
            weight="bold"
            color={theme.colors.gray800}
            css={css`
              font-size: 28px;
              line-height: 1.358;
              letter-spacing: -0.66px;
            `}
          >
            {event.title}
          </Text>

          <Flex dir="column">
            <Flex dir="row" gap="4px" alignItems="center">
              <Icon name="location" size={16} />
              <Text type="Label" weight="medium" color={theme.colors.gray500}>
                {event.place}
              </Text>
            </Flex>
            <Flex dir="row" gap="4px" alignItems="center">
              <Icon name="calendar" size={16} />
              <Text type="Label" weight="medium" color={theme.colors.gray500}>
                {formatDateTime(event.eventStart)} ~ {formatDateTime(event.eventEnd)}
              </Text>
            </Flex>
          </Flex>
        </Flex>
        <Spacing height="80px" />

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
            <Tabs.Trigger value="applications">참여 현황</Tabs.Trigger>
          </Tabs.List>

          <Tabs.Content value="detail">
            <EventInfoSection event={event} profile={profile} />
          </Tabs.Content>

          <Tabs.Content value="applications">
            <GuestManageSection />
          </Tabs.Content>
        </Tabs>

        <ButtonWrapper justifyContent="center">
          {/* E.TODO 마감됨 버튼이 너무커서 겹치는 이슈 -> footer 구현 후 해결 여부 확인 */}
          {isClosed ? (
            <Button size="full" color="tertiary" variant="solid" disabled>
              마감됨
            </Button>
          ) : (
            <Button size="full" color="tertiary" variant="solid" onClick={handleButtonClick}>
              마감하기
            </Button>
          )}
        </ButtonWrapper>
      </EventManageContainer>
    </PageLayout>
  );
};

const ButtonWrapper = styled(Flex)`
  display: flex;
  position: fixed;
  bottom: 0px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  width: 100%;
  max-width: 1072px;
  padding: 20px 0;

  > button {
    width: 100%;
  }

  @media (max-width: 768px) {
    padding: 20px 20px;
  }
`;
