import { queryOptions } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import type { OrganizationProfile, Profile } from '../types/profile';

export const profileQueryKeys = {
  all: () => ['profile'],
};

export const profileQueryOptions = {
  profile: () =>
    queryOptions({
      queryKey: [...profileQueryKeys.all()],
      queryFn: () => getProfile(),
    }),
  organizationProfile: (organizationId: number) =>
    queryOptions({
      queryKey: [...profileQueryKeys.all(), organizationId],
      queryFn: () => getOrganizationProfile(organizationId),
    }),
};

const getProfile = async () => {
  return await fetcher.get<Profile>('members/profile');
};

const getOrganizationProfile = async (organizationId: number) => {
  return await fetcher.get<OrganizationProfile>(`organizations/${organizationId}/profile`);
};
