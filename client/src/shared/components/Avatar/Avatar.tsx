import { useState } from 'react';

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

const DEFAULT_AVATAR_URL = 'https://ahmadda-dev.s3.ap-northeast-2.amazonaws.com/profile_avatar.png';

export const Avatar = ({ picture, name }: AvatarProps) => {
  const [currentSrc, setCurrentSrc] = useState(picture || DEFAULT_AVATAR_URL);

  const handleImageError = () => {
    if (currentSrc !== DEFAULT_AVATAR_URL) {
      setCurrentSrc(DEFAULT_AVATAR_URL);
    }
  };

  return (
    <Flex dir="row" gap="12px" alignItems="center">
      <StyledAvatarImage
        src={currentSrc}
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
