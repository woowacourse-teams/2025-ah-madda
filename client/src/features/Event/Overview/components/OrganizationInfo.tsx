import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Woowa from '@/assets/icon/wowaw.png';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { Organization } from '../../types/Event';

type OrganizationProps = {
  totalEvents: number;
} & Omit<Organization, 'organizationId'>;

// S.TODO : 추후 imageUrl 적용
export const OrganizationInfo = ({
  name,
  description,
  imageUrl,
  totalEvents = 0,
}: OrganizationProps) => {
  return (
    <Flex
      dir="column"
      width="100%"
      gap="20px"
      padding="60px 0 0 0"
      css={css`
        max-width: 1120px;
        margin: 0 auto;
      `}
    >
      <Flex padding="20px 10px" justifyContent="space-between" alignItems="center" width="100%">
        <Flex dir="column" gap="8px">
          <Text type="Display" weight="bold">
            {name}
          </Text>
          <Text as="h2" type="Heading">
            {description}
          </Text>
          <Text type="Body">{`${totalEvents}개의 이벤트가 열려있어요!`}</Text>
        </Flex>
        <Img src={Woowa} />
      </Flex>
    </Flex>
  );
};

const Img = styled.img`
  width: 100%;
  max-width: clamp(160px, 30vw, 252px);
  height: auto;
  margin-right: 20px;
  padding: 20px 0;
`;
