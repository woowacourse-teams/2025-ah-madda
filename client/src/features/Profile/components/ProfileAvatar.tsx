import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { DEFAULT_AVATAR_URL } from '@/shared/constants';

type ProfileAvatarProps = {
  src?: string;
  alt?: string;
};

export const ProfileAvatar = ({ src, alt = '프로필 이미지' }: ProfileAvatarProps) => {
  return (
    <Flex
      dir="column"
      gap="16px"
      justifyContent="center"
      alignItems="center"
      width="100%"
      css={css`
        border-radius: 50%;
        overflow: hidden;
      `}
    >
      <ProfileImage src={src || DEFAULT_AVATAR_URL} alt={alt} onError={() => {}} />
    </Flex>
  );
};

const ProfileImage = styled.img`
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 50%;
`;
