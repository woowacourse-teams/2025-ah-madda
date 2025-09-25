import { css } from '@emotion/react';
import styled from '@emotion/styled';

import DefaultImage from '@/assets/icon/ahmadda.webp';
import { Organization } from '@/features/Organization/types/Organization';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

type OrganizationProps = Omit<Organization, 'organizationId'>;

const THUMB_MAX_PX = 160;

export const OrganizationInfo = ({ name, description, imageUrl }: OrganizationProps) => {
  const src = imageUrl || DefaultImage;
  const alt = imageUrl ? `${name} 썸네일` : '기본 이벤트 스페이스 이미지';

  return (
    <Flex
      dir="column"
      width="100%"
      gap="20px"
      margin="0px auto"
      padding="80px 0 10px 0"
      css={css`
        @media (max-width: 768px) {
          padding: 80px 0 0 0;
        }
      `}
    >
      <Flex padding="0 10px" justifyContent="space-between" alignItems="center" width="100%">
        <Flex
          justifyContent="space-between"
          alignItems="center"
          padding="10px"
          css={css`
            @media (max-width: 481px) {
              flex-direction: column;
              align-items: flex-start;
            }
          `}
        >
          <ThumbWrap>
            <ThumbImg
              src={src}
              alt={alt}
              decoding="async"
              fetchPriority="high"
              onError={(e) => {
                e.currentTarget.onerror = null;
                e.currentTarget.src = DefaultImage;
              }}
            />
          </ThumbWrap>

          <Flex dir="column" gap="8px">
            <Text type="Display" weight="bold">
              {name}
            </Text>
            <Text as="h2" type="Heading">
              {description}
            </Text>
          </Flex>
        </Flex>
      </Flex>
    </Flex>
  );
};

const ThumbWrap = styled.div`
  position: relative;
  width: clamp(140px, 30vw, ${THUMB_MAX_PX}px);
  aspect-ratio: 1 / 1;
  border-radius: 12px;
  overflow: hidden;
  margin-right: 20px;
  background: #f2f3f5;

  @media (max-width: 481px) {
    margin-right: 0;
    margin-bottom: 8px;
  }
`;

const ThumbImg = styled.img`
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
`;
