import { css, keyframes } from '@emotion/react';
import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';

const shimmer = keyframes`
  0% { background-position: -200px 0; }
  100% { background-position: calc(200px + 100%) 0; }
`;

const SkelBlock = styled.div<{ h?: number }>`
  width: 100%;
  height: ${({ h }) => (h ? `${h}px` : '16px')};
  border-radius: 10px;
  background: #f2f3f5;
  background-image: linear-gradient(90deg, #f2f3f5 0px, #e9ecef 40px, #f2f3f5 80px);
  background-size: 200px 100%;
  animation: ${shimmer} 2.4s infinite linear;
`;

const Container = styled.div`
  max-width: 1120px;
  width: 100%;
  margin: 0 auto;
  padding: 0 10px;
`;

const Thumb = styled.div`
  width: clamp(140px, 30vw, 160px);
  aspect-ratio: 1 / 1;
  border-radius: 12px;
  overflow: hidden;
  background: #f2f3f5;
  background-image: linear-gradient(90deg, #f2f3f5 0px, #e9ecef 40px, #f2f3f5 80px);
  background-size: 200px 100%;
  animation: ${shimmer} 2.4s infinite linear;
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
`;

export const OrganizationInfoSkeleton = () => (
  <Container
    css={css`
      padding-top: 80px;
      padding-bottom: 10px;
      @media (max-width: 768px) {
        padding-top: 80px;
        padding-bottom: 0;
      }
    `}
  >
    <Flex alignItems="center" gap="20px" padding="10px">
      <Thumb />
      <Flex dir="column" gap="8px" style={{ flex: 1 }}>
        <SkelBlock h={28} />
        <SkelBlock h={20} />
      </Flex>
    </Flex>
  </Container>
);

export const TabsSkeleton = () => (
  <Container>
    <SkelBlock h={40} />
    <Flex
      css={css`
        height: 12px;
      `}
    />
    <Grid>
      {Array.from({ length: 6 }).map((_, i) => (
        <SkelBlock key={i} h={180} />
      ))}
    </Grid>
  </Container>
);
