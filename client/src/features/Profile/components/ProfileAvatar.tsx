import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { DEFAULT_AVATAR_URL } from '@/shared/constants';

type ProfileAvatarProps = {
  email: string;
  src?: string;
  alt?: string;
};

export const ProfileAvatar = ({ email, src, alt = '프로필 이미지' }: ProfileAvatarProps) => {
  return (
    <Flex dir="column" gap="16px" justifyContent="center" alignItems="center" width="100%">
      <ProfileImage src={src || DEFAULT_AVATAR_URL} alt={alt} width={80} height={80} />
      <Text type="Body" weight="semibold" color="gray900">
        {email}
      </Text>
    </Flex>
  );
};

const ProfileImage = styled.img`
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 50%;
`;
