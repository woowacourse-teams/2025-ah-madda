import { useSuspenseQueries } from '@tanstack/react-query';

import { profileQueryOptions } from '@/api/queries/profile';

export const useProfile = (organizationId: number) => {
  const [{ data: profile }, { data: organizationProfile }] = useSuspenseQueries({
    queries: [
      profileQueryOptions.profile(),
      profileQueryOptions.organizationProfile(organizationId),
    ],
  });

  return {
    profile,
    organizationProfile,
  };
};
