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
    if (!inviteCode) {
      alert('유효하지 않은 초대 링크입니다.');
      navigate('/');
      return;
    }

    if (!isAuthenticated()) {
      alert('로그인이 필요한 서비스입니다.');
      navigate('/');
      return;
    }
  }, [inviteCode, navigate]);

  const { data: organizationData } = useQuery({
    ...organizationQueryOptions.preview(inviteCode!),
    enabled: !!inviteCode && isAuthenticated(),
  });
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
          navigate(`/event?organizationId=${organizationData?.organizationId}`);
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
    handleJoin,
    handleClose,
  };
};
