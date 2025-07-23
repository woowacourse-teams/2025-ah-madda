import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { messages } from '../constants/messages';

import { MessageCard } from './MessageCard';

export const Description = () => {
  return (
    <Flex dir="column" justifyContent="center" alignItems="center" padding="30px 20px">
      <Flex dir="row" alignItems="center" gap="2px">
        <Text type="Title" weight="semibold">
          저희는 이런 문제를 해결하려&nbsp;
        </Text>
        <Text type="Title" weight="semibold" color="#2563EB">
          등장
        </Text>
        <Text type="Title" weight="semibold">
          했어요.
        </Text>
      </Flex>
      <GridContainer>
        {messages.map((message) => (
          <MessageCard key={message} text={message} />
        ))}
      </GridContainer>
    </Flex>
  );
};

const GridContainer = styled.div`
  width: 100%;
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;

  @media (min-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }

  padding: 20px 0 0 0;
`;
