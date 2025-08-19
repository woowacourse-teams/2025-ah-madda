import styled from '@emotion/styled';

import Woowa from '@/assets/icon/wowaw.png';
import { Organization } from '@/features/Event/types/Event';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

export const OrganizationInfo = ({ name }: Pick<Organization, 'name'>) => {
  return (
    <>
      <Img src={Woowa} alt={name} />
      <Text type="Body" weight="regular" color="#666">
        <Text as="span" type="Body" weight="bold" color={theme.colors.primary700}>
          {name}
        </Text>
        에서 사용할 닉네임을 입력해주세요.
      </Text>
    </>
  );
};

const Img = styled.img`
  width: 100%;
  max-width: 250px;
  height: auto;
  margin: 0 auto;
  padding: 20px 0;
`;
