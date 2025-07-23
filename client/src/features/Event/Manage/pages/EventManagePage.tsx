import { useNavigate } from 'react-router-dom';

import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';

import { EventInfoSection } from '../components/EventInfoSection';
import { GuestManageSection } from '../components/GuestManageSection';
import { EventManageContainer } from '../containers/EventManageContainer';

export const EventManagePage = () => {
  const navigate = useNavigate();

  return (
    <PageLayout
      header={
        <Header
          left={
            <Flex alignItems="center" gap="12px">
              <IconButton
                name="back"
                size={14}
                aria-label="이전 페이지로 돌아가기"
                onClick={() => navigate(-1)}
              />
              <Text as="h1" type="Title" weight="semibold">
                이벤트 관리
              </Text>
            </Flex>
          }
        />
      }
    >
      <EventManageContainer>
        <Flex as="main" gap="40px" width="100%" dir="column">
          <EventInfoSection />
          <GuestManageSection />
        </Flex>
      </EventManageContainer>
    </PageLayout>
  );
};
