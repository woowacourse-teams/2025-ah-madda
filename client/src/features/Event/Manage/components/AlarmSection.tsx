import { css } from '@emotion/react';
import { useParams } from 'react-router-dom';

import { useAddAlarm } from '@/api/mutations/useAddAlarm';
import { NotifyHistoryAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { trackSendAlarm } from '@/shared/lib/gaEvents';
import { theme } from '@/shared/styles/theme';

import { useModal } from '../../../../shared/hooks/useModal';
import { useNotificationForm } from '../hooks/useNotificationForm';

import { AlarmHistoryModal } from './AlarmHistoryModal';

export type AlarmSectionProps = {
  notifyData: NotifyHistoryAPIResponse[];
  organizationMemberIds: number[];
  selectedGuestCount: number;
};

export const AlarmSection = ({
  notifyData,
  organizationMemberIds,
  selectedGuestCount,
}: AlarmSectionProps) => {
  const { content, handleContentChange, resetContent } = useNotificationForm();
  const { eventId: eventIdParam } = useParams();
  const { mutate: postAlarm, isPending } = useAddAlarm({ eventId: Number(eventIdParam) });
  const { isOpen, open, close } = useModal();
  const { success, error } = useToast();

  const handleSendAlarm = () => {
    trackSendAlarm(selectedGuestCount);

    postAlarm(
      { organizationMemberIds, content },
      {
        onSuccess: () => {
          resetContent();
          success('알람이 성공적으로 전송되었습니다.');
        },
        onError: (err) => {
          error(err.message);
        },
      }
    );
  };

  return (
    <>
      <Card
        css={css`
          padding: 32px;
        `}
      >
        <Flex as="section" dir="column">
          <Flex
            justifyContent="space-between"
            alignItems="center"
            gap="8px"
            css={css`
              @media (max-width: 768px) {
                flex-direction: column;
                align-items: flex-start;
                gap: 16px;
              }
            `}
          >
            <Flex dir="column" gap="8px">
              <Text type="Heading" weight="bold" color={theme.colors.gray800}>
                선택된 그룹원에게 리마인드
              </Text>
              <Text type="Body" weight="medium" color={theme.colors.gray600}>
                {`선택한 ${selectedGuestCount}명에게 알람이 전송됩니다.`}
              </Text>
            </Flex>
            <Button size="sm" color="tertiary" onClick={open}>
              알림 내역
            </Button>
          </Flex>
          <Spacing height="28px" />
          <Flex dir="column" alignItems="center">
            <Input
              id="alarm-message"
              placeholder="알람 메시지를 입력해주세요"
              value={content}
              onChange={handleContentChange}
            />
            <Button
              size="full"
              color="primary"
              disabled={!content || organizationMemberIds.length === 0 || isPending}
              onClick={handleSendAlarm}
            >
              {isPending ? '전송 중...' : '보내기'}
            </Button>
          </Flex>
        </Flex>
      </Card>
      <AlarmHistoryModal notifyData={notifyData} isOpen={isOpen} onClose={close} />
    </>
  );
};
