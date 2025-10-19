import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useCloseEventRegistration } from '@/api/mutations/useCloseEventRegistration';
import { eventQueryOptions } from '@/api/queries/event';
import { Badge } from '@/shared/components/Badge';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Spacing } from '@/shared/components/Spacing';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';
import { theme } from '@/shared/styles/theme';
import { formatDate } from '@/shared/utils/dateUtils';

import { badgeText } from '../../utils/badgeText';
import { DeadlineModal } from '../components/DeadlineModal';
import { EventInfoSection } from '../components/EventInfoSection';
import { GuestManageSection } from '../components/GuestManageSection';
import { EventManageContainer } from '../containers/EventManageContainer';

export const EventManagePage = () => {
  const navigate = useNavigate();
  const { eventId: eventIdParam, organizationId } = useParams();
  const eventId = Number(eventIdParam);
  const { success, error } = useToast();
  const { isOpen, open, close } = useModal();
  const { data: event, refetch } = useQuery(eventQueryOptions.detail(eventId));
  const { data: statistics = [] } = useQuery(eventQueryOptions.statistic(eventId));
  const { mutate: closeEventRegistration } = useCloseEventRegistration();

  const isClosed = event?.registrationEnd ? new Date(event.registrationEnd) < new Date() : false;

  const handleDeadlineChangeClick = () => {
    closeEventRegistration(eventId, {
      onSuccess: () => {
        success('이벤트가 마감되었습니다.');
        refetch();
        close();
      },
      onError: (err) => {
        if (err instanceof HttpError) {
          error(err.message);
        }
      },
    });
  };

  if (!event) return null;

  return (
    <>
      <PageLayout
        header={
          <Header
            left={
              <Icon
                name="logo"
                size={55}
                onClick={() => navigate(`/${organizationId}/event`)}
                css={css`
                  cursor: pointer;
                `}
              />
            }
            right={
              <Flex alignItems="center" gap="8px">
                <Button size="sm" onClick={() => navigate(`/${organizationId}/event/my`)}>
                  내 이벤트
                </Button>
                <IconButton
                  name="user"
                  size={24}
                  onClick={() => navigate(`/${organizationId}/profile`)}
                />
              </Flex>
            }
          />
        }
      >
        <EventManageContainer>
          <Spacing height="56px" />
          <Flex dir="column" gap="12px">
            <Badge variant={badgeText(event.registrationEnd).color}>
              {badgeText(event.registrationEnd).text}
            </Badge>
            <Flex dir="row" justifyContent="space-between">
              <Text type="Title" weight="bold" color={theme.colors.gray900}>
                {event.title}
              </Text>

              {isClosed ? (
                <Button size="sm" color="tertiary" variant="solid" disabled>
                  마감됨
                </Button>
              ) : (
                <Flex alignItems="center" gap="8px">
                  <Button
                    size="sm"
                    color="primary"
                    variant="solid"
                    onClick={() => navigate(`/${organizationId}/event/${eventId}/edit`)}
                  >
                    수정하기
                  </Button>
                  <Button size="sm" color="secondary" variant="solid" onClick={open}>
                    마감하기
                  </Button>
                </Flex>
              )}
            </Flex>

            <Flex dir="column" gap="4px">
              <Flex dir="row" gap="4px" alignItems="center">
                <Icon name="location" size={16} color="gray500" />
                <Text type="Label" weight="medium" color={theme.colors.gray500}>
                  {event.place}
                </Text>
              </Flex>
              <Flex dir="row" gap="4px" alignItems="center">
                <Icon name="calendar" size={16} color="gray500" />
                <Text type="Label" weight="medium" color={theme.colors.gray500}>
                  {formatDate({
                    start: event.eventStart,
                    end: event.eventEnd,
                    pattern: 'YYYY. MM. DD A HH시',
                    options: {
                      dayOfWeek: 'shortParen',
                    },
                  })}
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
              <EventInfoSection event={event} statistics={statistics} />
            </Tabs.Content>

            <Tabs.Content value="applications">
              <GuestManageSection />
            </Tabs.Content>
          </Tabs>
        </EventManageContainer>
      </PageLayout>
      <DeadlineModal isOpen={isOpen} onClose={close} onDeadlineChange={handleDeadlineChangeClick} />
    </>
  );
};
