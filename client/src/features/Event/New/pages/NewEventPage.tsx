import { css } from '@emotion/react';
import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventCreateForm } from '../components/EventCreateForm';

export const NewEventPage = () => {
  const navigate = useNavigate();
  const { eventId } = useParams();
  const isEdit = !!eventId;

  return (
    <PageLayout
      header={
        <Header
          left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />}
          right={
            <Button size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          }
        />
      }
    >
      <Flex
        dir="column"
        width="100%"
        margin="0 auto"
        padding="28px 14px"
        gap="24px"
        css={css`
          max-width: 784px;
        `}
      >
        <EventCreateForm isEdit={isEdit} eventId={eventId ? Number(eventId) : undefined} />
      </Flex>
    </PageLayout>
  );
};
