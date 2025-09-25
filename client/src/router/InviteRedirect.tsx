import { useEffect } from 'react';

import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';

export const InviteRedirect = () => {
  const { organizationId, eventId } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const inviteCode = searchParams.get('code');
  const { data: isMember, error } = useQuery({
    ...organizationQueryOptions.profile(Number(organizationId)),
    retry: false,
  });

  useEffect(() => {
    if (isMember) {
      const eventRedirectTo = `/${organizationId}/event/${eventId}`;
      navigate(eventRedirectTo);
    } else if (error?.message === '존재하지 않는 구성원입니다.') {
      const inviteRedirect = `/invite?code=${inviteCode}`;
      navigate(inviteRedirect);
    }
  }, [eventId, organizationId, navigate, isMember, inviteCode, error]);

  return null;
};
