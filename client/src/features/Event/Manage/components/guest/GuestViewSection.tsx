import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { myQueryOptions } from '@/api/queries/my';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';
import { theme } from '@/shared/styles/theme';

import { Guest, NonGuest } from '../../types';

import { GuestAnswerModal } from './GuestAnswerModal';
import { GuestList } from './GuestList';

export type GuestViewSectionProps = {
  guests: Guest[];
  onGuestChecked: (organizationMemberId: number) => void;
  onAllChecked: VoidFunction;
  nonGuests: NonGuest[];
  onNonGuestChecked: (organizationMemberId: number) => void;
  onNonGuestAllChecked: VoidFunction;
};

type TabButtonProps = {
  isActive: boolean;
};

type TabBadgeProps = TabButtonProps;

export const GuestViewSection = ({
  guests,
  onGuestChecked,
  onAllChecked,
  nonGuests,
  onNonGuestChecked,
  onNonGuestAllChecked,
}: GuestViewSectionProps) => {
  const { eventId } = useParams();
  const [selectedGuest, setSelectedGuest] = useState<Guest | null>(null);
  const [activeTab, setActiveTab] = useState<'guests' | 'nonGuests'>('nonGuests');
  const { isOpen, open, close } = useModal();

  const { data: guestAnswers } = useQuery({
    ...myQueryOptions.event.guestAnswers(Number(eventId), selectedGuest?.guestId ?? 0),
    enabled: !!selectedGuest?.guestId,
  });

  const handleGuestClick = (guest: Guest | NonGuest) => {
    if ('guestId' in guest) {
      setSelectedGuest(guest);
      open();
    }
  };

  return (
    <>
      <Flex
        as="section"
        dir="column"
        gap="16px"
        padding="30px 0"
        css={css`
          min-height: 0;
        `}
      >
        <Flex
          padding="6px"
          gap="8px"
          css={css`
            background-color: ${theme.colors.gray100};
            border-radius: 12px;
          `}
        >
          <TabButton isActive={activeTab === 'guests'} onClick={() => setActiveTab('guests')}>
            <Text
              type="Body"
              weight="bold"
              color={activeTab === 'guests' ? theme.colors.gray800 : theme.colors.gray400}
            >
              신청
            </Text>
            <TabBadge isActive={activeTab === 'guests'}>
              <Text
                type="Label"
                weight="medium"
                color={activeTab === 'guests' ? theme.colors.white : theme.colors.gray100}
              >
                {guests.length}
              </Text>
            </TabBadge>
          </TabButton>

          <TabButton isActive={activeTab === 'nonGuests'} onClick={() => setActiveTab('nonGuests')}>
            <Text
              type="Body"
              weight="bold"
              color={activeTab === 'nonGuests' ? theme.colors.gray800 : theme.colors.gray400}
            >
              미신청
            </Text>
            <TabBadge isActive={activeTab === 'nonGuests'}>
              <Text
                type="Label"
                weight="medium"
                color={activeTab === 'nonGuests' ? theme.colors.white : theme.colors.gray100}
              >
                {nonGuests.length}
              </Text>
            </TabBadge>
          </TabButton>
        </Flex>

        <ScrollArea>
          {activeTab === 'guests' ? (
            <GuestList
              title={`신청 완료 (${guests.length}명)`}
              titleColor={theme.colors.primary600}
              guests={guests}
              onGuestChecked={onGuestChecked}
              onAllGuestChecked={onAllChecked}
              onGuestClick={handleGuestClick}
            />
          ) : (
            <GuestList
              title={`미신청 (${nonGuests.length}명)`}
              titleColor={theme.colors.gray700}
              guests={nonGuests}
              onGuestChecked={onNonGuestChecked}
              onAllGuestChecked={onNonGuestAllChecked}
              onGuestClick={handleGuestClick}
            />
          )}
        </ScrollArea>
      </Flex>

      <GuestAnswerModal
        isOpen={isOpen}
        onClose={close}
        guest={selectedGuest}
        guestAnswers={guestAnswers}
      />
    </>
  );
};

const ScrollArea = styled.div`
  max-height: 320px;
  overflow-y: auto;
`;

const TabButton = styled.button<TabButtonProps>`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 6px;
  background-color: ${({ isActive }) => (isActive ? theme.colors.white : theme.colors.gray100)};
  border: none;
  cursor: pointer;
  flex: 1;
  transition: all 0.2s ease;

  &:hover {
    background-color: ${({ isActive }) => (isActive ? theme.colors.white : theme.colors.gray50)};
  }
`;

const TabBadge = styled.div<TabBadgeProps>`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8px;
  border-radius: 999px;
  background-color: ${({ isActive }) =>
    isActive ? theme.colors.primary600 : theme.colors.gray400};
  min-width: 20px;
  height: 20px;
`;
