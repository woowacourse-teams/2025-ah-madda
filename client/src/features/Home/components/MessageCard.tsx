import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type MessageCardProps = {
  index: number;
  message: string;
  image: string;
};

export const MessageCard = ({ message, image, index }: MessageCardProps) => {
  return (
    <Flex
      role="article"
      dir="row"
      justifyContent="space-between"
      alignItems="flex-start"
      width="100%"
      padding="32px"
      css={css`
        background-color: ${theme.colors.gray100};
        border-radius: 12px;
      `}
      aria-label={`${index + 1}번째. ${message}`}
      tabIndex={0}
    >
      <Text as="h3" type="Heading" weight="semibold" aria-hidden="true">
        {message}
      </Text>
      <LandingImage
        src={image}
        alt=""
        width={600}
        height={300}
        loading={index > 2 ? 'lazy' : 'eager'}
        isSecond={index === 1}
      />
    </Flex>
  );
};

const LandingImage = styled.img<{ isSecond: boolean }>`
  width: ${({ isSecond }) => (isSecond ? '40%' : '30%')};
  height: auto;
  align-self: flex-end;

  @media (max-width: 768px) {
    width: 40%;
  }
`;
