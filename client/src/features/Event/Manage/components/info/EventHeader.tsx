import { useNavigate, useParams } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useCloseEventRegistration } from '@/api/mutations/useCloseEventRegistration';
import { badgeText } from '@/features/Event/utils/badgeText';
import { Badge } from '@/shared/components/Badge';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';
import { formatDate } from '@/shared/utils/dateUtils';

import { DeadlineModal } from './DeadlineModal';

type EventHeaderProps = {
  eventId: number;
  title: string;
  place: string;
  eventStart: string;
  eventEnd: string;
  registrationEnd: string;
};

export const EventHeader = ({
  eventId,
  title,
  place,
  eventStart,
  eventEnd,
  registrationEnd,
}: EventHeaderProps) => {
  const { success, error } = useToast();
  const navigate = useNavigate();
  const { organizationId } = useParams();
  const isClosed = registrationEnd ? new Date(registrationEnd) < new Date() : false;
  const { isOpen, open, close } = useModal();

  const badgeTextValue = badgeText(registrationEnd);

  const { mutate: closeEventRegistration } = useCloseEventRegistration();

  const handleDeadlineChangeClick = () => {
    closeEventRegistration(eventId, {
      onSuccess: () => {
        success('이벤트가 마감되었습니다.');
        close();
      },
      onError: (err) => {
        if (err instanceof HttpError) {
          error(err.message);
        }
      },
    });
  };

  return (
    <>
      <Flex dir="column" gap="12px">
        <Badge variant={badgeTextValue.color}>{badgeTextValue.text}</Badge>
        <Flex dir="row" justifyContent="space-between">
          <Text as="h1" type="Display" weight="bold">
            {title}
          </Text>

          {isClosed ? (
            <Button size="sm" color="tertiary" variant="solid" disabled>
              마감됨
            </Button>
          ) : (
            <Flex alignItems="center" gap="8px">
              <Button
                size="md"
                color="primary"
                variant="solid"
                onClick={() => navigate(`/${organizationId}/event/${eventId}/edit`)}
              >
                수정하기
              </Button>
              <Button size="md" color="secondary" variant="solid" onClick={open}>
                마감하기
              </Button>
            </Flex>
          )}
        </Flex>

        <Flex dir="column" gap="4px">
          <Flex dir="row" gap="4px" alignItems="center">
            <Icon name="location" size={18} color="gray500" />
            <Text type="Label">{place}</Text>
          </Flex>
          <Flex dir="row" gap="4px" alignItems="center">
            <Icon name="calendar" size={18} color="gray500" />
            <Text type="Label">
              {formatDate({
                start: eventStart,
                end: eventEnd,
                pattern: 'YYYY. MM. DD A HH시',
                options: {
                  dayOfWeek: 'shortParen',
                },
              })}
            </Text>
          </Flex>
        </Flex>
      </Flex>
      <DeadlineModal isOpen={isOpen} onClose={close} onDeadlineChange={handleDeadlineChangeClick} />
    </>
  );
};
