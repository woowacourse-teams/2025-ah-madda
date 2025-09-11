import { css } from '@emotion/react';
import styled from '@emotion/styled';

import DefaultImage from '@/assets/icon/ahmadda.webp';
import { Organization } from '@/features/Organization/types/Organization';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

type OrganizationProps = Omit<Organization, 'organizationId'>;

export const OrganizationInfo = ({ name, description, imageUrl }: OrganizationProps) => {
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
          <Img
            src={imageUrl || DefaultImage}
            alt={imageUrl ? `${name} 썸네일` : '기본 이벤트 스페이스 이미지'}
            onError={(e) => {
              e.currentTarget.onerror = null;
              e.currentTarget.src = DefaultImage;
            }}
          />
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

const Img = styled.img`
  width: 100%;
  max-width: clamp(140px, 30vw, 160px);
  height: auto;
  margin-right: 20px;
  padding: 20px 0;
`;
