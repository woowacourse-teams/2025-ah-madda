import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();
  return (
    <>
      <Header
        left={<Icon name="logo" width={55} />}
        right={
          <Flex gap="8px">
            <Button width="80px" size="sm" variant="outlined" fontColor="#2563EB">
              로그아웃
            </Button>
            <Button width="80px" size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          </Flex>
        }
      />

      <OrganizationInfo />
      <EventList />
    </>
  );
};
