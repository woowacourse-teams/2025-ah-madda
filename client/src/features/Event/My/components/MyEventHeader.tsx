import { useNavigate } from 'react-router-dom';

import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { Text } from '@/shared/components/Text';

import { UI_LABELS } from '../constants';

export const MyEventHeader = () => {
  const navigate = useNavigate();
  return (
    <Header
      left={
        <Flex alignItems="center" gap="12px">
          <IconButton
            name="back"
            size={14}
            aria-label="이전 페이지로 돌아가기"
            onClick={() => navigate(-1)}
          />
          <Text as="h1" type="Title" weight="bold">
            {UI_LABELS.PAGE_TITLE}
          </Text>
        </Flex>
      }
    />
  );
};
