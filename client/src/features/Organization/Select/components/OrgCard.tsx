import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

export type Org = {
  organizationId: number;
  name: string;
  imageUrl: string;
  description: string;
};

export type OrgCardProps = {
  org: Org;
  onJoin: () => void;
};

export const OrgCard = ({ org, onJoin }: OrgCardProps) => (
  <Flex
    dir="column"
    alignItems="center"
    role="button"
    aria-label={`${org.name} 참여하기`}
    onClick={onJoin}
    css={css`
      cursor: pointer;
      width: 120px;
    `}
  >
    <Flex
      width="120px"
      height="120px"
      justifyContent="center"
      alignItems="center"
      css={css`
        border: 2px solid ${theme.colors.gray100};
        overflow: hidden;
        position: relative;

        &:hover .overlay {
          opacity: 1;
        }

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          display: block;
        }
      `}
    >
      <img
        src={org.imageUrl}
        alt={org.name}
        onError={(e) => {
          e.currentTarget.src = '/icon-512x512.png';
        }}
      />

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

    <Text
      css={css`
        margin-top: 8px;
        text-align: center;
      `}
    >
      {org.name}
    </Text>
  </Flex>
);
