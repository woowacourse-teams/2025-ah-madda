import { queryOptions } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { ProfileAPIResponse } from '../types/profile';

export const profileQueryKeys = {
  all: () => ['profile'],
};

export const profileQueryOptions = {
  profile: () =>
    queryOptions({
      queryKey: [...profileQueryKeys.all()],
      queryFn: () => getProfile(),
    }),
};

const getProfile = async () => {
  return await fetcher.get<ProfileAPIResponse>('open-profiles');
};
