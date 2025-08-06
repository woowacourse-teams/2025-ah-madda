import { useState } from 'react';

import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type TemplateModalProps = {
  isOpen: boolean;
  onClose: () => void;
};

export const TemplateModal = ({ isOpen, onClose }: TemplateModalProps) => {
  //E.TODO organizationId 받아오기
  const { data: eventTitles } = useQuery(eventQueryOptions.titles(1));
  const [selectedEvent, setSelectedEvent] = useState<number>(0);

  return (
    <Modal isOpen={isOpen} onClose={onClose} showCloseButton={false}>
      <Flex dir="column" gap="24px" width="100%">
        <Text type="Title" weight="bold" color={theme.colors.gray900}>
          템플릿 불러오기
        </Text>

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
            const isSelected = selectedEvent === event.eventId;

            return (
              <Card
                key={event.eventId}
                onClick={() => setSelectedEvent(event.eventId)}
                css={css`
                  cursor: pointer;
                  padding: 16px;
                  background-color: ${isSelected ? theme.colors.primary50 : theme.colors.white};
                  border-radius: 8px;
                  transition: all 0.2s ease;
                  &:hover {
                    background-color: ${theme.colors.primary50};
                  }
                `}
              >
                <Flex alignItems="center" gap="12px">
                  <Text type="Body" weight="medium" color={theme.colors.gray900}>
                    {event.title}
                  </Text>
                </Flex>
              </Card>
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

        <Spacing height="1px" />

        <Flex gap="12px" justifyContent="flex-end">
          <Button color="secondary" variant="outline" onClick={onClose}>
            취소
          </Button>
          <Button color="primary" onClick={() => {}}>
            불러오기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
