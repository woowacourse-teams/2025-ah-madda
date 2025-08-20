import { useCallback, useEffect } from 'react';

import { useQuery } from '@tanstack/react-query';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { useParticipateOrganization } from '@/api/mutations/useParticipateOrganization';
import { organizationQueryOptions } from '@/api/queries/organization';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';

export const useInviteOrganizationProcess = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { success, error } = useToast();
  const { close } = useModal();

  const inviteCode = searchParams.get('code');

  const validateAndRedirect = useCallback(() => {
    if (!inviteCode) {
      error('유효하지 않은 초대 링크입니다.', { duration: 3000 });
      navigate('/', { replace: true });
      return;
    }

    if (!isAuthenticated()) {
      error('로그인이 필요한 서비스입니다.', { duration: 3000 });
      navigate('/', { replace: true });
      return;
    }
  }, [inviteCode, error, navigate]);

  useEffect(() => {
    validateAndRedirect();
  }, [validateAndRedirect]);

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
          success('조직 참가가 완료되었습니다!');
          close();
          navigate(`/event?organizationId=${organizationData?.organizationId}`);
        },
        onError: (err) => {
          error(err.message, { duration: 3000 });
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
