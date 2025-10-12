import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQueries } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { myQueryOptions } from '@/api/queries/my';
import { GuestAnswerAPIResponse } from '@/api/types/my';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import type { Guest, NonGuest } from '../types';

import { GuestList } from './GuestList';

type GuestViewSectionProps = {
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
type TabKey = 'guests' | 'nonGuests' | 'answers';

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
  const [activeTab, setActiveTab] = useState<TabKey>('answers');

  const answerResults = useQueries({
    queries: guests.map((guest) => ({
      ...myQueryOptions.event.guestAnswers(Number(eventId), guest.guestId),
      enabled: activeTab === 'answers',
    })),
  });

  return (
    <>
      <Flex as="section" dir="column" gap="20px" padding="30px">
        <Text type="Heading" weight="bold" color={theme.colors.gray800}>
          게스트 조회
        </Text>

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

          <TabButton isActive={activeTab === 'answers'} onClick={() => setActiveTab('answers')}>
            <Text
              type="Body"
              weight="bold"
              color={activeTab === 'answers' ? theme.colors.gray800 : theme.colors.gray400}
            >
              사전 질문
            </Text>
          </TabButton>
        </Flex>

        {activeTab === 'guests' && (
          <GuestList
            title={`신청 완료 (${guests.length}명)`}
            titleColor={theme.colors.primary600}
            guests={guests}
            onGuestChecked={onGuestChecked}
            onAllGuestChecked={onAllChecked}
          />
        )}

        {activeTab === 'nonGuests' && (
          <GuestList
            title={`미신청 (${nonGuests.length}명)`}
            titleColor={theme.colors.gray700}
            guests={nonGuests}
            onGuestChecked={onNonGuestChecked}
            onAllGuestChecked={onNonGuestAllChecked}
          />
        )}

        {activeTab === 'answers' && (
          <AnswersPanel>
            {guests.length === 0 && (
              <EmptyState>
                <Text type="Body" weight="medium" color={theme.colors.gray500}>
                  아직 신청한 참여자가 없어요.
                </Text>
              </EmptyState>
            )}

            {guests.map((g, idx) => {
              const result = answerResults[idx];
              const isError = !!result?.error;
              const items = (result?.data as GuestAnswerAPIResponse[] | undefined) ?? [];

              return (
                <AnswerCard key={g.organizationMemberId}>
                  <NickNameHeading>
                    <Text type="Heading" weight="bold" color={theme.colors.gray800}>
                      {g.nickname}
                    </Text>
                  </NickNameHeading>

                  {isError && (
                    <Text type="Label" weight="medium" color={theme.colors.gray800}>
                      답변을 불러오지 못했습니다.
                    </Text>
                  )}

                  {!isError && items.length === 0 && (
                    <Text type="Label" weight="medium" color={theme.colors.gray500}>
                      작성한 답변이 없습니다.
                    </Text>
                  )}

                  {!isError && items.length > 0 && (
                    <QAList>
                      {items.map((qa, i) => (
                        <QAItem key={qa.orderIndex ?? i}>
                          <Text type="Body" weight="bold" color={theme.colors.gray800}>
                            {qa.questionText}
                          </Text>
                          <Text type="Body" weight="medium" color={theme.colors.gray700}>
                            {qa.answerText}
                          </Text>
                        </QAItem>
                      ))}
                    </QAList>
                  )}
                </AnswerCard>
              );
            })}
          </AnswersPanel>
        )}
      </Flex>
    </>
  );
};

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

const AnswersPanel = styled.section`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

const AnswerCard = styled.article`
  border: 1px solid ${theme.colors.gray100};
  border-radius: 12px;
  padding: 16px;
`;

const NickNameHeading = styled.header`
  margin-bottom: 8px;
`;

const QAList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

const QAItem = styled.div`
  display: grid;
  padding: 8px 0;
`;

const EmptyState = styled.div`
  padding: 16px 0;
  text-align: center;
`;
