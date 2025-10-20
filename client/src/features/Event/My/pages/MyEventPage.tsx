import { css } from '@emotion/react';
import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventTabs } from '../components/EventTabs';
import { Info } from '../components/Info';
import { MyEventContainer } from '../containers/MyEventContainer';

export const MyEventPage = () => {
  const navigate = useNavigate();
  const { organizationId } = useParams();
  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="logo"
              size={55}
              onClick={() => navigate(`/${organizationId}/event`)}
              css={css`
                cursor: pointer;
              `}
            />
          }
          right={
            <Flex alignItems="center" gap="8px">
              <Button size="sm" onClick={() => navigate(`/${organizationId}/event/my`)}>
                내 이벤트
              </Button>
              <IconButton
                name="user"
                size={24}
                onClick={() => navigate(`/${organizationId}/profile`)}
              />
            </Flex>
          }
        />
      }
    >
      <MyEventContainer>
        <Info />
        <EventTabs />
      </MyEventContainer>
    </PageLayout>
  );
};
