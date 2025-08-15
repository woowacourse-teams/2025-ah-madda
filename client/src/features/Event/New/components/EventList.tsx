import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { SelectableCard } from './SelectableCard';

type EventListItem = {
  eventId: number;
  title: string;
};

type EventListProps = {
  events: EventListItem[];
  selectedId: number | null;
  onSelectEvent: (eventId: number | null) => void;
};

export const EventList = ({ events, selectedId, onSelectEvent }: EventListProps) => {
  return (
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
        {events.map((event) => {
          const isSelected = selectedId !== null && selectedId === event.eventId;

          return (
            <SelectableCard
              key={event.eventId}
              isSelected={isSelected}
              onClick={() => onSelectEvent(event.eventId)}
            >
              <Flex justifyContent="space-between" alignItems="center">
                <Text type="Body" weight="medium" color={theme.colors.gray900}>
                  {event.title.length > 21 ? `${event.title.slice(0, 21)}...` : event.title}
                </Text>
              </Flex>
            </SelectableCard>
          );
        })}
      </Flex>

      {events.length === 0 && (
        <Flex alignItems="center" justifyContent="center" padding="40px 0">
          <Text type="Body" weight="regular" color={theme.colors.gray500}>
            사용 가능한 템플릿이 없습니다.
          </Text>
        </Flex>
      )}
    </Flex>
  );
};
