import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

type InformationProps = {
  index: number;
  text: string;
  imageUrl: string;
};

export const Information = ({ index, text, imageUrl }: InformationProps) => {
  return (
    <Flex dir="column" gap="8px" width="100%" padding="10px 20px">
      <Text type="Body" weight="semibold">
        {index}. {text}
      </Text>
      <Flex dir="row" justifyContent="center" width="100%">
        <Img src={imageUrl} alt={imageUrl} />
      </Flex>
    </Flex>
  );
};

const Img = styled.img`
  max-width: clamp(300px, 30vw, 500px);
  height: auto;
  border-radius: 8px;
  margin-top: 8px;
`;
