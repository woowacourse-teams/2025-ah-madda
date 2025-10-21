import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { LANDING } from '../constants/messages';

import { MessageCard } from './MessageCard';

export const Description = () => {
  return (
    <Flex dir="column" justifyContent="center" alignItems="center" padding="30px 20px">
      <Flex
        role="heading"
        aria-level={2}
        dir="row"
        alignItems="center"
        aria-label="저희는 이런 문제를 해결하려고 등장했어요."
        tabIndex={0}
      >
        <Text as="h2" type="Title" weight="semibold" aria-hidden="true">
          저희는 이런 문제를 해결하려고
          <Text as="span" type="Title" weight="semibold" color={theme.colors.primary700}>
            {' '}
            등장
          </Text>
          했어요.
        </Text>
      </Flex>
      <GridContainer>
        {LANDING.map((landing, index) => (
          <MessageCard key={index} index={index} {...landing} />
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
