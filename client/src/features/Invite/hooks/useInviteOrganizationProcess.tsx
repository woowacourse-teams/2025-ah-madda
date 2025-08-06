import { useEffect } from 'react';

import { useQuery } from '@tanstack/react-query';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { useParticipateOrganization } from '@/api/mutations/useParticipateOrganization';
import { organizationQueryOptions } from '@/api/queries/organization';
import { useModal } from '@/shared/hooks/useModal';

export const useInviteOrganizationProcess = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { close } = useModal();

  const inviteCode = searchParams.get('code');

  useEffect(() => {
    if (!inviteCode || !isAuthenticated()) {
      navigate('/');
      if (!isAuthenticated()) {
        alert('로그인이 필요한 서비스입니다.');
      }
    }
  }, [inviteCode, navigate]);

  const { data: organizationData } = useQuery(organizationQueryOptions.preview(inviteCode ?? ''));
  const { mutate: joinOrganization } = useParticipateOrganization(
    organizationData?.organizationId ?? 0
  );

  const handleJoin = (nickname: string) => {
    joinOrganization(
      { nickname, inviteCode: inviteCode ?? '' },
      {
        onSuccess: () => {
          alert('조직 참가가 완료되었습니다!');
          close();
          navigate('/event');
        },
        onError: (error) => {
          alert(`${error.message}`);
          navigate('/');
        },
      }
    );
  };

  const handleClose = () => {
    close();
    navigate('/');
  };

  return {
    organizationData,
    isPending,
    handleJoin,
    handleClose,
  };
};
