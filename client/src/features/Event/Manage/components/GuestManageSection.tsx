import { css } from '@emotion/react';

import { Flex } from '@/shared/components/Flex';

import { Guest } from '../types';

import { AlarmSection } from './AlarmSection';
import { GuestViewSection } from './GuestViewSection';

type GuestManageSectionProps = {
  completedGuests: Guest[];
  pendingGuests: Guest[];
};

export const GuestManageSection = ({ completedGuests, pendingGuests }: GuestManageSectionProps) => {
  return (
    <Flex
      as="section"
      dir="column"
      gap="24px"
      width="100%"
      margin="10px"
      css={css`
        max-width: 800px;
        margin: 0 auto;
        padding: 0 16px;

        @media (max-width: 768px) {
          padding: 0 20px;
        }

        @media (max-width: 480px) {
          padding: 0 16px;
        }
      `}
    >
      <AlarmSection pendingGuestsCount={pendingGuests.length} />

      <GuestViewSection
        completedGuests={completedGuests}
        pendingGuests={pendingGuests}
        onGuestClick={() => {}}
      />
    </Flex>
  );
};
