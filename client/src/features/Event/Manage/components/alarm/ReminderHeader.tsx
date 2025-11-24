import { css } from '@emotion/react';

import type { NotifyHistoryAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';
import { theme } from '@/shared/styles/theme';

import { AlarmHistoryModal } from './AlarmHistoryModal';

type AlarmHeaderProps = {
  selectedGuestCount: number;
  notifyData: NotifyHistoryAPIResponse[];
};

export const AlarmHeader = ({ selectedGuestCount, notifyData }: AlarmHeaderProps) => {
  const { isOpen, open, close } = useModal();

  return (
    <>
      <Flex
        justifyContent="space-between"
        alignItems="center"
        gap="8px"
        css={css`
          @media (max-width: 768px) {
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
        <Button size="md" color="tertiary" variant="ghost" onClick={open}>
          알림 내역
        </Button>
      </Flex>

      <AlarmHistoryModal notifyData={notifyData} isOpen={isOpen} onClose={close} />
    </>
  );
};
