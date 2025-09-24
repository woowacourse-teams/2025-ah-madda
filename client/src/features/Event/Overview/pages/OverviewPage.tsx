import { useState } from 'react';

import { css } from '@emotion/react';
import { useSuspenseQueries } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { createInviteCode } from '@/api/mutations/useCreateInviteCode';
import { eventQueryOptions } from '@/api/queries/event';
import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';

import { ActionButtons } from '../components/ActionButtons';
import { InviteCodeModal } from '../components/InviteCodeModal';
import { OrganizationInfo } from '../components/OrganizationInfo';
import { OverviewTabs } from '../components/OverviewTabs';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const { organizationId } = useParams();
  const { error } = useToast();

  const [{ data: organizationData }, { data: currentEventData }, { data: pastEventData }] =
    useSuspenseQueries({
      queries: [
        organizationQueryOptions.organizations(String(organizationId)),
        eventQueryOptions.ongoing(Number(organizationId)),
        eventQueryOptions.past(Number(organizationId)),
      ],
    });

  const goMyEvents = () => navigate(`/${organizationId}/event/my`);
  const goHome = () => navigate(`/${organizationId}/event`);
  const goProfile = () => navigate(`/${organizationId}/profile`);

  const [inviteCode, setInviteCode] = useState('');
  const { isOpen, open, close } = useModal();

  const handleCreateInviteCode = async () => {
    try {
      const data = await createInviteCode(Number(organizationId));
      const baseUrl =
        process.env.NODE_ENV === 'production' ? 'https://ahmadda.com' : 'http://localhost:5173';
      const inviteUrl = `${baseUrl}/invite?code=${data.inviteCode}`;
      setInviteCode(inviteUrl);
      open();
    } catch {
      error('초대 코드 생성에 실패했습니다.');
    }
  };

  return (
    <>
      <PageLayout
        header={
          <Header
            left={
              <Icon
                name="logo"
                size={55}
                onClick={goHome}
                css={css`
                  cursor: pointer;
                `}
              />
            }
            right={
              <Button size="sm" onClick={goMyEvents}>
                내 이벤트
              </Button>
            }
          />
        }
      >
        <OrganizationInfo
          name={organizationData?.name ?? ''}
          description={organizationData?.description ?? ''}
          imageUrl={organizationData?.imageUrl ?? ''}
        />
        <ActionButtons onIssueInviteCode={handleCreateInviteCode} />
        <OverviewTabs currentEventData={currentEventData} pastEventData={pastEventData} />
      </PageLayout>
      <InviteCodeModal inviteCode={inviteCode} isOpen={isOpen} onClose={close} />
    </>
  );
};
