import { css } from '@emotion/react';

import { Flex } from '../../../shared/components/Flex';
import { Header } from '../../../shared/components/Header';
import { IconButton } from '../../../shared/components/IconButton';
import { PageLayout } from '../../../shared/components/PageLayout';
import { Text } from '../../../shared/components/Text';
import { EventCreateForm } from '../components/EventCreateForm';

export const CreateEvent = () => {
  return (
    <PageLayout
      header={
        <Header
          left={
            <Flex alignItems="center" gap="12px">
              <IconButton name="back" size={14} />
              <Text type="caption">돌아가기</Text>
            </Flex>
          }
          css={css`
            background-color: white;
          `}
        />
      }
    >
      <Flex
        dir="column"
        width="100%"
        margin="0 auto"
        padding="28px 14px"
        gap="24px"
        css={css`
          max-width: 784px;
          box-sizing: border-box;
        `}
      >
        <EventCreateForm />
      </Flex>
    </PageLayout>
  );
};
