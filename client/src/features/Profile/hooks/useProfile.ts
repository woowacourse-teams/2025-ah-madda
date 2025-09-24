import { useSuspenseQueries } from '@tanstack/react-query';

import { organizationQueryOptions } from '@/api/queries/organization';
import { profileQueryOptions } from '@/api/queries/profile';

export const useProfile = (organizationId: number) => {
  const [{ data: profile }, { data: organizationProfile }, { data: organizationGroups }] =
    useSuspenseQueries({
      queries: [
        profileQueryOptions.profile(),
        profileQueryOptions.organizationProfile(organizationId),
        organizationQueryOptions.group(),
      ],
    });

  return {
    profile,
    organizationProfile,
    organizationGroups,
  };
};
