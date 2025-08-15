import { SyntheticEvent } from 'react';

import { useSuspenseQueries } from '@tanstack/react-query';

import { profileQueryOptions } from '@/api/queries/profile';

import { Flex } from '../Flex';
import { Text } from '../Text';

import { StyledAvatarImage } from './Avatar.styled';

export const Avatar = () => {
  const [{ data: profile }] = useSuspenseQueries({ queries: [profileQueryOptions.profile()] });

  const handleImageError = (event: SyntheticEvent<HTMLImageElement>) => {
    event.currentTarget.style.display = 'none';
  };

  return (
    <Flex dir="row" gap="12px" alignItems="center">
      <StyledAvatarImage
        src={profile?.picture}
        alt={`${profile?.name}의 프로필 이미지`}
        onError={handleImageError}
        width="40px"
        height="40px"
      />
      <Text type="Body" weight="medium" color="gray700">
        {profile?.name}
      </Text>
    </Flex>
  );
};
