import { queryOptions } from '@tanstack/react-query';

import { Event, Organization } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';
import { OrganizerStatusAPIResponse } from '../types/event';

export const organizationQueryKeys = {
  all: () => ['organization'],
  event: () => [...organizationQueryKeys.all(), 'event'],
  profile: () => [...organizationQueryKeys.all(), 'profile'],
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

  profile: (organizationId: number) =>
    queryOptions({
      queryKey: [...organizationQueryKeys.profile(), organizationId],
      queryFn: () => getOrganizationProfile({ organizationId }),
    }),
};

const getAllEventAPI = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<Event[]>(`organizations/${organizationId}/events`);
};

const getOrganizerStatus = async (eventId: number) => {
  return await fetcher.get<OrganizerStatusAPIResponse>(
    `organizations/events/${eventId}/organizer-status`
  );
};

const getOrganization = ({ organizationId }: { organizationId: string }) => {
  return fetcher.get<Organization>(`organizations/${organizationId}`);
};

const getOrganizationProfile = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<{ nickname: string }>(`organizations/${organizationId}/profile`);
};
