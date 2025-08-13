import { css } from '@emotion/react';

import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';

import { InviteModal } from '../component/InviteModal';

export const InvitePage = () => {
  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="logo"
              size={55}
              css={css`
                cursor: pointer;
              `}
            />
          }
        />
      }
    >
      <InviteModal />
    </PageLayout>
  );
};
