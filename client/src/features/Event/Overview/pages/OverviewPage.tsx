import { Suspense, useState } from 'react';

import { css } from '@emotion/react';
import { useSuspenseQuery, useSuspenseQueries } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { createInviteCode } from '@/api/mutations/useCreateInviteCode';
import { eventQueryOptions } from '@/api/queries/event';
import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';

import { ActionButtons } from '../components/ActionButtons';
import { InviteCodeModal } from '../components/InviteCodeModal';
import { OrganizationInfo } from '../components/OrganizationInfo';
import { OrganizationInfoSkeleton, TabsSkeleton } from '../components/OverviewSkeletons';
import { OverviewTabs } from '../components/OverviewTabs';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const { organizationId } = useParams();
  const { error } = useToast();

  const goMyEvents = () => navigate(`/${organizationId}/event/my`);
  const goHome = () => navigate(`/`);

  const [inviteCode, setInviteCode] = useState('');
  const { isOpen, open, close } = useModal();

  const orgIdNum = Number(organizationId);

  const handleCreateInviteCode = async () => {
    try {
      const data = await createInviteCode(orgIdNum);
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
                마이 페이지
              </Button>
            }
          />
        }
      >
        <Suspense fallback={<OrganizationInfoSkeleton />}>
          <OrganizationInfoSection organizationId={organizationId!} />
        </Suspense>

        <ActionButtons onIssueInviteCode={handleCreateInviteCode} />

        <Suspense fallback={<TabsSkeleton />}>
          <OverviewTabsSection organizationId={orgIdNum} />
        </Suspense>
      </PageLayout>

      <InviteCodeModal inviteCode={inviteCode} isOpen={isOpen} onClose={close} />
    </>
  );
};

const OrganizationInfoSection = ({ organizationId }: { organizationId: string }) => {
  const { data: organizationData } = useSuspenseQuery({
    ...organizationQueryOptions.organizations(String(organizationId)),
    staleTime: 5 * 60 * 1000,
  });

  return (
    <OrganizationInfo
      name={organizationData?.name ?? ''}
      description={organizationData?.description ?? ''}
      imageUrl={organizationData?.imageUrl ?? ''}
    />
  );
};

const OverviewTabsSection = ({ organizationId }: { organizationId: number }) => {
  const [{ data: currentEventData }, { data: pastEventData }] = useSuspenseQueries({
    queries: [
      {
        ...eventQueryOptions.ongoing(organizationId),
      },
      {
        ...eventQueryOptions.past(organizationId),
        staleTime: 5 * 60 * 1000,
      },
    ],
  });

  return <OverviewTabs currentEventData={currentEventData} pastEventData={pastEventData} />;
};
