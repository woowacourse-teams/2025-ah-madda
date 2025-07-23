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
          <EventInfoSection
            eventId={1}
            title="이벤트 제목"
            description="이벤트 설명"
            place="이벤트 장소"
            organizerName="주최자"
            eventStart="2025-01-01 10:00"
            eventEnd="2025-01-01 12:00"
            registrationStart="2025-01-01 09:00"
            registrationEnd="2025-01-01 11:00"
            currentGuestCount={10}
            maxCapacity={100}
            questions={[
              {
                questionId: 1,
                questionText: '이벤트 질문',
                isRequired: true,
                orderIndex: 1,
              },
            ]}
          />
          <GuestManageSection />
        </Flex>
      </EventManageContainer>
    </PageLayout>
  );
};
