import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { useCreateProfile } from '@/api/mutations/useCreateProfile';
import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { useGoogleAuth } from '@/shared/hooks/useGoogleAuth';
import { useModal } from '@/shared/hooks/useModal';

import { EventList } from '../components/EventList';
import { NicknameModal } from '../components/NicknameModal';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const { isOpen, open, close } = useModal();

  const { data: organizationData, isLoading: isOrganizationLoading } = useQuery(
    organizationQueryOptions.organizations('woowacourse')
  );

  const { data: eventData, isLoading: isEventLoading } = useQuery(
    organizationQueryOptions.event(1)
  );

  const { error: profileError } = useQuery(organizationQueryOptions.profile(1));

  const { logout } = useGoogleAuth();
  const createProfileMutation = useCreateProfile(1);

  const isProfileNotFound = profileError?.message.includes('404');

  const handleNicknameSubmit = (nickname: string) => {
    createProfileMutation.mutate(nickname, {
      onSuccess: () => {
        close();
        alert('조직 참가가 완료되었습니다!');
      },
      onError: () => {
        alert('참가 중 오류가 발생했습니다. 다시 시도해 주세요.');
      },
    });
  };

  const handleGoogleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
  };

  if (isEventLoading || isOrganizationLoading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Header
        left={<Icon name="logo" width={55} />}
        right={
          <Flex gap="8px">
            {isAuthenticated() ? (
              <Button
                width="80px"
                size="sm"
                variant="outlined"
                onClick={logout}
                color="#18A0FB"
                fontColor="#18A0FB"
              >
                로그아웃
              </Button>
            ) : (
              <Button
                width="80px"
                size="sm"
                variant="filled"
                fontColor="#FFF"
                onClick={handleGoogleLogin}
              >
                로그인
              </Button>
            )}
            <Button width="80px" size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          </Flex>
        }
      />

      <OrganizationInfo
        name={organizationData?.name || ''}
        description={organizationData?.description || ''}
        imageUrl={organizationData?.imageUrl || ''}
        totalEvents={eventData?.length || 0}
      />

      {isProfileNotFound ? (
        <Flex dir="column" justifyContent="center" alignItems="center" gap="16px" margin="40px 0">
          <Text type="Body" weight="regular" color="#666">
            조직에 참가하여 이벤트 목록을 확인하세요!
          </Text>
          <Button width="200px" size="md" variant="filled" onClick={open}>
            참가하기
          </Button>
        </Flex>
      ) : (
        <EventList events={eventData ?? []} />
      )}

      <NicknameModal isOpen={isOpen} onClose={close} onSubmit={handleNicknameSubmit} />
    </>
  );
};
