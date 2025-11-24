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
    <Card
      role="article"
      dir="row"
      justifyContent="space-between"
      alignItems="flex-start"
      width="100%"
      padding="32px"
      aria-label={`${index + 1}번째. ${message}`}
      tabIndex={0}
    >
      <TextContainer>
        <Text as="h3" type="Heading" weight="semibold" aria-hidden="true">
          {message}
        </Text>
      </TextContainer>

      <LandingImage
        src={image}
        alt=""
        width={600}
        height={300}
        loading={index > 2 ? 'lazy' : 'eager'}
        isSecond={index === 1}
      />
    </Card>
  );
};

const Card = styled(Flex)`
  background-color: ${theme.colors.gray100};
  border-radius: 12px;
  overflow: hidden;
  height: 250px;

  @media (max-width: 768px) {
    height: auto;
    padding: 24px;
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
`;

const TextContainer = styled.div`
  flex: 1;
  white-space: pre-line;
  word-break: keep-all;
  line-height: 1.4;
  max-width: 60%;

  @media (max-width: 768px) {
    font-size: clamp(0.95rem, 3.5vw, 1.2rem);
    line-height: 1.3;
    white-space: normal;
    max-width: 100%;
  }
`;

const LandingImage = styled.img<{ isSecond: boolean }>`
  width: ${({ isSecond }) => (isSecond ? '40%' : '30%')};
  height: auto;
  max-height: 200px;
  object-fit: contain;
  align-self: flex-end;

  @media (max-width: 768px) {
    width: 55%;
    max-height: 160px;
    align-self: end;
  }
`;
