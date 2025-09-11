import { css } from '@emotion/react';

import { DEFAULT_AVATAR_URL } from '@/shared/components/Avatar/Avatar';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';

import { ProfileAvatar } from '../components/ProfileAvatar';
import { ProfileForm } from '../components/ProfileForm';
import { useNicknameForm } from '../hooks/useNicknameForm';
import { useProfile } from '../hooks/useProfile';
import { useProfileNavigation } from '../hooks/useProfileNavigation';

export const ProfilePage = () => {
  const { organizationId, goBack } = useProfileNavigation();
  const { profile, organizationProfile } = useProfile(organizationId);
  const { nickname, handleNicknameChange, handleSaveNickname, hasChanges, isLoading } =
    useNicknameForm({
      organizationId,
      initialNickname: organizationProfile.nickname,
    });

  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="back"
              size={24}
              onClick={goBack}
              css={css`
                cursor: pointer;
              `}
            />
          }
        />
      }
    >
      <Card
        css={css`
          padding: 24px;
          margin-top: 80px;
        `}
      >
        <Flex dir="column" gap="24px">
          <Text type="Heading" weight="bold" color="gray900">
            프로필 정보
          </Text>

          <ProfileAvatar src={profile.picture || DEFAULT_AVATAR_URL} />

          <ProfileForm
            nickname={nickname}
            email={profile.email}
            hasChanges={hasChanges}
            isLoading={isLoading}
            onNicknameChange={handleNicknameChange}
            onSave={handleSaveNickname}
          />
        </Flex>
      </Card>
    </PageLayout>
  );
};
