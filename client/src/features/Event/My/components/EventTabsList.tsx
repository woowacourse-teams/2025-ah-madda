import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';
import { Tabs } from '@/shared/components/Tabs';

import { UI_LABELS, TAB_VALUES } from '../constants';

export const EventTabsList = () => {
  return (
    <Flex>
      <Tabs.List
        css={css`
          width: 40%;
          @media (max-width: 768px) {
            width: 100%;
          }
        `}
      >
        <Tabs.Trigger value={TAB_VALUES.HOST}>{UI_LABELS.HOST_TAB}</Tabs.Trigger>
        <Tabs.Trigger value={TAB_VALUES.PARTICIPATE}>{UI_LABELS.PARTICIPATE_TAB}</Tabs.Trigger>
      </Tabs.List>
    </Flex>
  );
};
