import { queryOptions } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { Profile } from '../types/profile';

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
  return await fetcher.get<Profile>('members/profile');
};
