import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

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
  const { data: event } = useQuery(eventQueryOptions.detail(eventId));

  const navigate = useNavigate();

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
          <Flex justifyContent="space-between">
            <Flex alignItems="center" gap="8px">
              <Icon name="calendar" size={14} />
              <Text type="Body" weight="regular" color="#4A5565">
                이벤트 정보
              </Text>
            </Flex>
            {/* A.TODO: 주최자 확인 API 연결 후 주석 해제 예정 */}
            {/* {isOrganizer && ( */}
            <Flex justifyContent="flex-end">
              <Button
                color="secondary"
                variant="outline"
                onClick={() => navigate(`/event/edit/${eventId}`)}
              >
                수정
              </Button>
            </Flex>
            {/* )} */}
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
