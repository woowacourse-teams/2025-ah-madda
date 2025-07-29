import { queryOptions } from '@tanstack/react-query';

import { Event, Organization } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

export const organizationQueryKeys = {
  all: () => ['organization'],
  event: () => [...organizationQueryKeys.all(), 'event'],
};
export const organizationQueryOptions = {
  // S.TODO : 추후 수정 ':organizationId' : number
  organizations: (organizationId: string) =>
    queryOptions({
      queryKey: [...organizationQueryKeys.event(), organizationId],
      queryFn: () => getOrganization({ organizationId }),
    }),

  event: (organizationId: number) =>
    queryOptions({
      queryKey: [...organizationQueryKeys.event(), organizationId],
      queryFn: () => getAllEventAPI({ organizationId }),
    }),
};

const getAllEventAPI = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<Event[]>(`organizations/${organizationId}/events`);
};

const getOrganization = ({ organizationId }: { organizationId: string }) => {
  return fetcher.get<Organization>(`organizations/${organizationId}`);
};
