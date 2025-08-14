import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useSuspenseQueries } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Spacing } from '@/shared/components/Spacing';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type TemplateModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onConfirmEvent: (eventId: number) => void;
  onConfirmTemplate: (templateId: number) => void;
  onSelect: (eventId: number) => void;
  selectedEventId: number;
};

type StyledCardProps = {
  isSelected: boolean;
};

export const TemplateModal = ({
  isOpen,
  onClose,
  onConfirmEvent,
  onConfirmTemplate,
}: TemplateModalProps) => {
  const [selectedType, setSelectedType] = useState<'template' | 'event' | null>(null);
  const [selectedId, setSelectedId] = useState(0);

  // E.TODO organizationId 받아오기
  const [{ data: eventTitles }, { data: templateList }] = useSuspenseQueries({
    queries: [eventQueryOptions.titles(1), eventQueryOptions.templateList()],
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
    if (selectedType === 'template' && selectedId > 0) {
      onConfirmTemplate(selectedId);
    } else if (selectedType === 'event' && selectedId > 0) {
      onConfirmEvent(selectedId);
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
            <Flex dir="column" gap="16px" padding="20px 0">
              <Text type="Body" weight="regular" color={theme.colors.gray600}>
                저장된 템플릿이 여기에 표시됩니다.
              </Text>
              {templateList?.map((template) => {
                const isSelected =
                  selectedType === 'template' && selectedId === template.templateId;

                return (
                  <StyledCard
                    key={template.templateId}
                    isSelected={isSelected}
                    onClick={() => handleSelectTemplate(template.templateId)}
                  >
                    <Text type="Body" weight="medium" color={theme.colors.gray900}>
                      {template.title}
                    </Text>
                  </StyledCard>
                );
              })}

              {templateList?.length === 0 && (
                <Flex alignItems="center" justifyContent="center" padding="40px 0">
                  <Text type="Body" weight="regular" color={theme.colors.gray500}>
                    아직 저장된 템플릿이 없습니다.
                  </Text>
                </Flex>
              )}
            </Flex>
          </Tabs.Content>

          <Tabs.Content value="my-events">
            <Flex dir="column" gap="16px" padding="20px 0">
              <Text type="Body" weight="regular" color={theme.colors.gray600}>
                이전에 만든 이벤트를 템플릿으로 사용할 수 있습니다.
              </Text>

              <Flex
                dir="column"
                gap="12px"
                css={css`
                  max-height: 200px;
                  overflow-y: auto;

                  &::-webkit-scrollbar {
                    width: 6px;
                  }

                  &::-webkit-scrollbar-thumb {
                    background: ${theme.colors.gray300};
                    border-radius: 3px;

                    &:hover {
                      background: ${theme.colors.gray400};
                    }
                  }
                `}
              >
                {eventTitles?.map((event) => {
                  const isSelected = selectedType === 'event' && selectedId === event.eventId;

                  return (
                    <StyledCard
                      key={event.eventId}
                      onClick={() => handleSelectEvent(event.eventId)}
                      isSelected={isSelected}
                    >
                      <Flex
                        alignItems="center"
                        gap="12px"
                        css={css`
                          overflow: hidden;
                        `}
                      >
                        <Text as="span" type="Body" weight="medium" color={theme.colors.gray900}>
                          {event.title}
                        </Text>
                      </Flex>
                    </StyledCard>
                  );
                })}
              </Flex>

              {eventTitles?.length === 0 && (
                <Flex alignItems="center" justifyContent="center" padding="40px 0">
                  <Text type="Body" weight="regular">
                    사용 가능한 템플릿이 없습니다.
                  </Text>
                </Flex>
              )}
            </Flex>
          </Tabs.Content>
        </Tabs>

        <Spacing height="1px" />

        <Flex gap="12px" justifyContent="center">
          <Button color="secondary" variant="outline" size="full" onClick={handleClose}>
            취소
          </Button>
          <Button color="primary" size="full" disabled={selectedId === 0} onClick={handleConfirm}>
            불러오기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};

const StyledCard = styled(Card)<StyledCardProps>`
  cursor: pointer;
  padding: 16px;
  background-color: ${({ isSelected }) =>
    isSelected ? theme.colors.primary50 : theme.colors.white};
  border-radius: 8px;
  transition: all 0.2s ease;
  &:hover {
    background-color: ${theme.colors.primary50};
  }
`;
