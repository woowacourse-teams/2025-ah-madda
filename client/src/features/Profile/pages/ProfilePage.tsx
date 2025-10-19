import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';
import { DEFAULT_AVATAR_URL } from '@/shared/constants';

import { ProfileAvatar } from '../components/ProfileAvatar';
import { ProfileForm } from '../components/ProfileForm';
import { useProfile } from '../hooks/useProfile';
import { useProfileNavigation } from '../hooks/useProfileNavigation';

export const ProfilePage = () => {
  const navigate = useNavigate();
  const { organizationId } = useProfileNavigation();
  const { profile, organizationProfile, organizationGroups } = useProfile(organizationId);

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
      <Flex width="100%" margin="0px auto" padding="120px 20px 10px ">
        <Flex dir="column" gap="24px" width="100%">
          <Text as="h1" type="Display" weight="bold" color="gray900">
            프로필 정보
          </Text>

          <ProfileAvatar email={profile.email} src={profile.picture || DEFAULT_AVATAR_URL} />
          <ProfileForm
            organizationProfile={organizationProfile}
            organizationGroups={organizationGroups}
            organizationId={organizationId}
          />
        </Flex>
      </Flex>
    </PageLayout>
  );
};
