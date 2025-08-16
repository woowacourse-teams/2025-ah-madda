import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';
import { theme } from '@/shared/styles/theme';

type Org = {
  organizationId: number;
  name: string;
  thumbnailUrl: string;
  description: string;
};

export const OrgCard = ({ org, onJoin }: { org: Org; onJoin: () => void }) => (
  <Flex
    width="120px"
    height="120px"
    role="button"
    aria-label={`${org.name} 참여하기`}
    onClick={onJoin}
    css={css`
      position: relative;
      border: 2px solid ${theme.colors.gray100};
      cursor: pointer;

      &:hover .overlay {
        opacity: 1;
      }
    `}
  >
    <Flex
      width="100%"
      height="100%"
      css={css`
        object-fit: cover;
        display: block;
      `}
    >
      <img src={org.thumbnailUrl || '/icon-512x512.png'} alt={org.name} />
    </Flex>

    <Flex
      className="overlay"
      justifyContent="center"
      alignItems="center"
      css={css`
        position: absolute;
        inset: 0;
        background: rgba(0, 0, 0, 0.44);
        color: #fff;
        font-weight: 700;
        opacity: 0;
        transition: opacity 0.18s ease-in-out;
      `}
    >
      참여하기
    </Flex>
  </Flex>
);
