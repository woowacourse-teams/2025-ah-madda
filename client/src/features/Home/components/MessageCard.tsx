import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

type MessageCardProps = {
  index: number;
  message: string;
  image: string;
};

export const MessageCard = ({ message, image, index }: MessageCardProps) => {
  return (
    <Flex
      dir="column"
      alignItems="flex-start"
      width="100%"
      padding="32px"
      css={css`
        background-color: #f8f9fa;
        border-radius: 12px;
      `}
    >
      <Text type="Heading" weight="semibold">
        {message}
      </Text>
      <LandingImage
        src={image}
        alt={image}
        width={600}
        height={300}
        loading={index > 2 ? 'lazy' : 'eager'}
        sizes="(max-width: 768px) 50vw, 60vw"
      />
    </Flex>
  );
};

const LandingImage = styled.img`
  width: 60%;
  height: auto;
  align-self: flex-end;
  aspect-ratio: 3 / 2;

  @media (max-width: 768px) {
    width: 50%;
  }
`;
