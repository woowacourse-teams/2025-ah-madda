import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { createInviteCode } from '@/api/mutations/useCreateInviteCode';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { useModal } from '../../../../shared/hooks/useModal';
import { EventCard } from '../../components/EventCard';
import { Event, Organization } from '../../types/Event';
import { groupEventsByDate } from '../../utils/groupEventsByDate';
import { EventContainer } from '../containers/EventContainer';

import { ActionButtons } from './ActionButtons';
import { EventSection } from './EventSection';
import { InviteCodeModal } from './InviteCodeModal';

type EventListProps = {
  events: Event[];
} & Pick<Organization, 'organizationId'>;

export const EventList = ({ organizationId, events }: EventListProps) => {
  const [inviteCode, setInviteCode] = useState('');
  const groupedEvents = groupEventsByDate(events);
  const { isOpen, open, close } = useModal();

  const handleCreateInviteCode = async () => {
    try {
      const data = await createInviteCode(Number(organizationId));
      const baseUrl =
        process.env.NODE_ENV === 'production' ? 'https://ahmadda.com' : 'http://localhost:5173';
      const inviteUrl = `${baseUrl}/invite?code=${data.inviteCode}`;
      setInviteCode(inviteUrl);
      open();
    } catch {
      alert('초대 코드 생성에 실패했습니다.');
    }
  };

  return (
    <>
      <EventContainer>
        <Flex
          width="100%"
          justifyContent="flex-start"
          alignItems="center"
          gap="5px"
          padding="10px"
          css={css`
            border-radius: 8px;
            background-color: ${theme.colors.primary50};
          `}
        >
          <Icon name="calendar" color="primary500" size={20} />
          <Text weight="bold" color={theme.colors.primary500}>
            {events.length}개의 이벤트가 열려있어요!
          </Text>
        </Flex>
        <Flex dir="column" width="100%" gap="20px">
          <ActionButtons onIssueInviteCode={handleCreateInviteCode} />
          {groupedEvents.length === 0 ? (
            <Flex justifyContent="center" alignItems="center" height="200px">
              <Text type="Heading" weight="semibold">
                등록된 이벤트가 없습니다.
              </Text>
            </Flex>
          ) : (
            groupedEvents.map(({ label, events }) => (
              <EventSection key={label} title={label}>
                <EventGrid>
                  {events.map((event, index) => (
                    <EventCard key={index} {...event} cardType="default" />
                  ))}
                </EventGrid>
              </EventSection>
            ))
          )}
        </Flex>
      </EventContainer>
      <InviteCodeModal inviteCode={inviteCode} isOpen={isOpen} onClose={close} />
    </>
  );
};

export const EventGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;

  @media (min-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (min-width: 1024px) {
    grid-template-columns: repeat(3, 1fr);
  }
`;
