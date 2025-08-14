import { useState } from 'react';

import { css } from '@emotion/react';
import { useSuspenseQueries, useQuery } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import type { EventTemplateAPIResponse, TemplateDetailAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Spacing } from '@/shared/components/Spacing';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { EventList } from './EventList';
import { TemplateList } from './TemplateList';

type TemplateModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onTemplateSelected: (templateDetail: Pick<TemplateDetailAPIResponse, 'description'>) => void;
  onEventSelected: (eventData: Omit<EventTemplateAPIResponse, 'eventId'>) => void;
};

export const TemplateModal = ({
  isOpen,
  onClose,
  onTemplateSelected,
  onEventSelected,
}: TemplateModalProps) => {
  const [selectedType, setSelectedType] = useState<'template' | 'event' | null>(null);
  const [selectedId, setSelectedId] = useState(0);

  // E.TODO organizationId 받아오기
  const [{ data: eventTitles }, { data: templateList }] = useSuspenseQueries({
    queries: [eventQueryOptions.titles(1), eventQueryOptions.templateList()],
  });

  const { data: selectedTemplateData } = useQuery({
    ...eventQueryOptions.templateDetail(selectedId),
    enabled: selectedType === 'template' && selectedId > 0,
  });

  const { data: selectedEventData } = useQuery({
    ...eventQueryOptions.pastEventList(selectedId),
    enabled: selectedType === 'event' && selectedId > 0,
  });

  const handleSelectTemplate = (templateId: number) => {
    setSelectedId(templateId);
    setSelectedType('template');
  };

  const handleSelectEvent = (eventId: number) => {
    setSelectedId(eventId);
    setSelectedType('event');
  };

  const handleConfirm = () => {
    if (selectedType === 'template' && selectedTemplateData) {
      onTemplateSelected({ description: selectedTemplateData.description });
    } else if (selectedType === 'event' && selectedEventData) {
      onEventSelected({
        title: selectedEventData.title,
        description: selectedEventData.description,
        place: selectedEventData.place,
        maxCapacity: selectedEventData.maxCapacity,
      });
    }

    onClose();
    setSelectedType(null);
    setSelectedId(0);
  };

  const handleClose = () => {
    onClose();
    setSelectedType(null);
    setSelectedId(0);
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
      <Flex dir="column" gap="24px" width="100%">
        <Text type="Title" weight="bold" color={theme.colors.gray900}>
          템플릿 불러오기
        </Text>

        <Tabs defaultValue="my-templates">
          <Tabs.List>
            <Tabs.Trigger value="my-templates">나만의 템플릿</Tabs.Trigger>
            <Tabs.Trigger value="my-events">나의 이벤트</Tabs.Trigger>
          </Tabs.List>

          <Tabs.Content value="my-templates">
            <TemplateList
              templates={templateList || []}
              selectedId={selectedType === 'template' ? selectedId : 0}
              onSelectTemplate={handleSelectTemplate}
            />
          </Tabs.Content>

          <Tabs.Content value="my-events">
            <EventList
              events={eventTitles || []}
              selectedId={selectedType === 'event' ? selectedId : 0}
              onSelectEvent={handleSelectEvent}
            />
          </Tabs.Content>
        </Tabs>

        <Spacing height="1px" />

        <Flex gap="12px" justifyContent="center">
          <Button color="secondary" variant="outline" size="full" onClick={handleClose}>
            취소
          </Button>
          <Button
            color="primary"
            size="full"
            disabled={
              selectedId === 0 ||
              (selectedType === 'template' && !selectedTemplateData) ||
              (selectedType === 'event' && !selectedEventData)
            }
            onClick={handleConfirm}
          >
            불러오기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
