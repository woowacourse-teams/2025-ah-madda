import { SyntheticEvent } from 'react';

import { DEFAULT_AVATAR_URL } from '@/shared/constants';

import { Flex } from '../Flex';
import { Text } from '../Text';

import { StyledAvatarImage } from './Avatar.styled';

type AvatarProps = {
  /**
   * The profile picture URL or null if no picture is available.
   * @type {string | null}
   */
  picture: string | null;
  /**
   * The display name for the avatar.
   * @type {string}
   */
  name: string;
};

export const Avatar = ({ picture, name }: AvatarProps) => {
  const handleImageError = (e: SyntheticEvent<HTMLImageElement, Event>) => {
    e.currentTarget.src = DEFAULT_AVATAR_URL;
  };

  return (
    <Flex dir="row" gap="12px" alignItems="center">
      <StyledAvatarImage
        src={picture ?? DEFAULT_AVATAR_URL}
        alt="프로필 이미지"
        onError={handleImageError}
        width="40px"
        height="40px"
      />

      <Text type="Body" weight="medium" color="gray700">
        {name}
      </Text>
    </Flex>
  );
};
