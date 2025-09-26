import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Skeleton } from '@/shared/components/Skeleton';

export const OrganizationInfoSkeleton = () => (
  <Container
    css={css`
      padding-top: 80px;
      padding-bottom: 10px;

      @media (max-width: 768px) {
        padding-bottom: 0;
      }
    `}
  >
    <Flex
      alignItems="center"
      gap="20px"
      padding="10px"
      css={css`
      @media (max-width: 481px) {
        flex-direction: column;
        align-items: flex-start;
      `}
    >
      <Skeleton width="clamp(140px, 30vw, 160px)" height="clamp(140px, 30vw, 160px)" />
      <Flex dir="column" gap="8px" style={{ flex: 1 }}>
        <Skeleton width="200px" height="28px" />
        <Skeleton width="200px" height="20px" />
      </Flex>
    </Flex>
  </Container>
);

export const TabsSkeleton = () => (
  <Container
    css={css`
      margin-top: 37.5px;
    `}
  >
    <Skeleton height="40px" />
    <Flex height="12px" />
    <Grid>
      {Array.from({ length: 6 }).map((_, i) => (
        <Skeleton key={i} height="180px" />
      ))}
    </Grid>
  </Container>
);

const Container = styled.div`
  max-width: 1120px;
  width: 100%;
  margin: 0 auto;
  padding: 0 10px;
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
`;
