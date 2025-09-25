import { useState } from 'react';

import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import type { EventTemplateAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { ModalProps } from '@/shared/components/Modal/Modal';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { EventList } from './EventList';

type TemplateModalProps = { organizationId: number } & Pick<ModalProps, 'isOpen' | 'onClose'> & {
    onEventSelected: (eventData: Omit<EventTemplateAPIResponse, 'eventId'>) => void;
  };

export const MyPastEventModal = ({
  organizationId,
  isOpen,
  onClose,
  onEventSelected,
}: TemplateModalProps) => {
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const { data: eventTitles } = useQuery(eventQueryOptions.titles(Number(organizationId)));

  const { data: selectedEventData } = useQuery({
    ...eventQueryOptions.pastEventList(selectedId!),
    enabled: selectedId !== null && selectedId > 0,
  });

  const handleSelectEvent = (eventId: number | null) => {
    setSelectedId(eventId);
  };

  const handleConfirm = () => {
    if (selectedEventData) {
      onEventSelected({
        title: selectedEventData.title,
        description: selectedEventData.description,
        place: selectedEventData.place,
        maxCapacity: selectedEventData.maxCapacity,
      });
    }

    onClose();
    setSelectedId(null);
  };

  const handleClose = () => {
    onClose();
    setSelectedId(null);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      showCloseButton={false}
      css={css`
        width: 400px;
      `}
    >
      <Flex dir="column" width="100%">
        <Text type="Title" weight="bold" color={theme.colors.gray900}>
          나의 이벤트 불러오기
        </Text>

        <EventList
          events={eventTitles || []}
          selectedId={selectedId}
          onSelectEvent={handleSelectEvent}
        />

        <Spacing height="1px" />

        <Flex gap="12px" justifyContent="center">
          <Button color="secondary" variant="outline" size="full" onClick={handleClose}>
            취소
          </Button>
          <Button
            color="primary"
            size="full"
            disabled={selectedId === null || !selectedEventData}
            onClick={handleConfirm}
          >
            불러오기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
