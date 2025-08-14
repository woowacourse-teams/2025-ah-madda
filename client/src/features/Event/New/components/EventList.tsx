import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type Event = {
  eventId: number;
  title: string;
};

type EventListProps = {
  events: Event[];
  selectedId: number;
  onSelectEvent: (eventId: number) => void;
};

type StyledCardProps = {
  isSelected: boolean;
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
        {events?.map((event) => {
          const isSelected = selectedId === event.eventId;

          return (
            <StyledCard
              key={event.eventId}
              onClick={() => onSelectEvent(event.eventId)}
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

      {events?.length === 0 && (
        <Flex alignItems="center" justifyContent="center" padding="40px 0">
          <Text type="Body" weight="regular">
            사용 가능한 템플릿이 없습니다.
          </Text>
        </Flex>
      )}
    </Flex>
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
