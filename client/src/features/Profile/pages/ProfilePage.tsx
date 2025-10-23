import { useSuspenseQueries } from '@tanstack/react-query';

import { organizationQueryOptions } from '@/api/queries/organization';
import { profileQueryOptions } from '@/api/queries/profile';
import { Flex } from '@/shared/components/Flex';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';
import { DEFAULT_AVATAR_URL } from '@/shared/constants';

import { ProfileAvatar } from '../components/ProfileAvatar';
import { ProfileForm } from '../components/ProfileForm';

export const ProfilePage = () => {
  const [{ data: profile }, { data: group }] = useSuspenseQueries({
    queries: [profileQueryOptions.profile(), organizationQueryOptions.group()],
  });

  return (
    <PageLayout>
      <Flex width="100%" margin="0px auto" padding="120px 20px 10px ">
        <Flex dir="column" gap="24px" width="100%">
          <Text as="h1" type="Display" weight="bold" color="gray900">
            프로필 정보
          </Text>

          <ProfileAvatar email={profile.email} src={profile.picture || DEFAULT_AVATAR_URL} />
          <ProfileForm profile={profile} totalGroup={group} />
        </Flex>
      </Flex>
    </PageLayout>
  );
};
