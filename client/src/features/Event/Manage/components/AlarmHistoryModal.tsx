import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { NotifyHistoryAPIResponse } from '@/api/types/event';
import { Badge } from '@/shared/components/Badge';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { ModalProps } from '@/shared/components/Modal/Modal';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { HistoryContainer } from '../containers/HistoryContainer';

import { HistoryCard } from './HistoryCard';

type AlarmHistoryModalProps = { notifyData: NotifyHistoryAPIResponse[] } & ModalProps;

export const AlarmHistoryModal = ({ notifyData, isOpen, onClose }: AlarmHistoryModalProps) => {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 380px;
      `}
    >
      <Text as="h3" type="Heading" weight="bold">
        알림 내역
      </Text>
      <Spacing height="12px" />
      <HistoryContainer>
        {notifyData.length === 0 ? (
          <Flex height="200px" justifyContent="center" alignItems="center">
            <Text type="Body" weight="regular">
              알림 내역이 없습니다.
            </Text>
          </Flex>
        ) : (
          notifyData.map((history, index) => <HistoryCard key={index} {...history} />)
        )}
      </HistoryContainer>
    </Modal>
  );
};
