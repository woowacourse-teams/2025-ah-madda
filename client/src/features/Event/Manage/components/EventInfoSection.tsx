import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useCloseEventRegistration } from '@/api/mutations/useCloseEventRegistration';
import { eventQueryOptions } from '@/api/queries/event';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';

import { formatDateTime } from '../../My/utils/date';

export const EventInfoSection = () => {
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

  return (
    <Flex
      as="section"
      dir="column"
      gap="24px"
      width="100%"
      margin="0 auto"
      padding="0 16px"
      css={css`
        max-width: 800px;

        @media (max-width: 768px) {
          padding: 0 20px;
        }

        @media (max-width: 480px) {
          padding: 0 16px;
        }
      `}
    >
      <Card>
        <Flex dir="column" gap="16px">
          <Flex alignItems="center" gap="8px">
            <Icon name="calendar" size={14} />
            <Flex dir="row" width="100%" justifyContent="space-between" alignItems="center">
              <Text type="Body" weight="regular" color="#4A5565">
                이벤트 정보
              </Text>
              {isClosed ? (
                <Button size="sm" color="tertiary" variant="solid" disabled>
                  마감됨
                </Button>
              ) : (
                <Button size="sm" color="tertiary" variant="solid" onClick={handleButtonClick}>
                  마감하기
                </Button>
              )}
            </Flex>
          </Flex>

          <Text type="Body" weight="semibold" color="#0A0A0A">
            {event?.title}
          </Text>

          <Text type="Label" weight="regular" color="#4A5565">
            {event?.description}
          </Text>

          <Flex alignItems="center" gap="8px">
            <Icon name="user" size={14} />
            <Text type="Label" weight="regular" color="#4A5565">
              {`주최자: ${event?.organizerName}`}
            </Text>
          </Flex>

          <Flex alignItems="center" gap="8px">
            <Icon name="location" size={14} />
            <Text type="Label" weight="regular" color="#4A5565">
              {event?.place}
            </Text>
          </Flex>

          <Flex alignItems="center" gap="8px">
            <Icon name="calendar" size={14} />
            <Text type="Label" weight="regular" color="#4A5565">
              {`신청 마감: ${formatDateTime(event?.registrationEnd ?? '')}`}
            </Text>
          </Flex>

          <Flex alignItems="center" gap="8px">
            <Icon name="clock" size={14} />
            <Text type="Label" weight="regular" color="#4A5565">
              {`이벤트 일시: ${formatDateTime(event?.eventStart ?? '')} ~ ${formatDateTime(
                event?.eventEnd ?? ''
              )}`}
            </Text>
          </Flex>
          <Spacing height="1px" color="#ECEEF2" />

          <Flex dir="column" gap="12px">
            <Flex justifyContent="space-between" alignItems="center">
              <Flex alignItems="center" gap="8px">
                <Icon name="user" size={14} />
                <Text type="Label" weight="regular" color="#4A5565">
                  참가 현황
                </Text>
              </Flex>
              <Text type="Label" weight="regular" color="#4A5565">
                {`${event?.currentGuestCount}/${event?.maxCapacity}명`}
              </Text>
            </Flex>
            <ProgressBar
              value={event?.currentGuestCount ?? 0}
              max={event?.maxCapacity ?? 0}
              color="#0A0A0A"
            />
          </Flex>
        </Flex>
      </Card>
    </Flex>
  );
};
