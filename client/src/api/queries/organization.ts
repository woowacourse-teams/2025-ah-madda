import { queryOptions } from '@tanstack/react-query';

import { Event } from '@/features/Event/types/Event';
import { Organization } from '@/features/Organization/Select/types/Organization';

import { fetcher } from '../fetcher';
import { OrganizationProfileAPIResponse } from '../types/organizations';

export const organizationQueryKeys = {
  all: () => ['organization'],
  event: () => [...organizationQueryKeys.all(), 'event'],
  profile: () => [...organizationQueryKeys.all(), 'profile'],
  preview: () => [...organizationQueryKeys.all(), 'preview'],
  participated: () => [...organizationQueryKeys.all(), 'participated'],
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

  preview: (inviteCode: string) =>
    queryOptions({
      queryKey: [...organizationQueryKeys.all(), 'preview', inviteCode],
      queryFn: () => getOrganizationPreview(inviteCode),
    }),

  participated: () =>
    queryOptions({
      queryKey: organizationQueryKeys.participated(),
      queryFn: getParticipatedOrganizations,
    }),
};

const getAllEventAPI = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<Event[]>(`organizations/${organizationId}/events`);
};

const getOrganization = ({ organizationId }: { organizationId: string }) => {
  return fetcher.get<Organization>(`organizations/${organizationId}`);
};

const getOrganizationProfile = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<OrganizationProfileAPIResponse>(`organizations/${organizationId}/profile`);
};

export const getOrganizationPreview = (inviteCode: string) => {
  return fetcher.get<Organization>(`organizations/preview?inviteCode=${inviteCode}`);
};

export const getParticipatedOrganizations = () => {
  return fetcher.get<Organization[]>(`organizations/participated`);
};
