import { queryOptions } from '@tanstack/react-query';

import { Event } from '@/features/Event/types/Event';
import { Organization } from '@/features/Organization/types/Organization';

import { fetcher } from '../fetcher';
import { OrganizationMember } from '../types/organizations';

export const organizationQueryKeys = {
  all: () => ['organization'],
  event: () => [...organizationQueryKeys.all(), 'event'],
  profile: () => [...organizationQueryKeys.all(), 'profile'],
  preview: () => [...organizationQueryKeys.all(), 'preview'],
  joined: () => [...organizationQueryKeys.all(), 'participated'],
  members: () => [...organizationQueryKeys.all(), 'organization-members'],
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

  joined: () =>
    queryOptions({
      queryKey: organizationQueryKeys.joined(),
      queryFn: getParticipatedOrganizations,
    }),
  members: (organizationId: number) =>
    queryOptions({
      queryKey: [...organizationQueryKeys.members(), organizationId],
      queryFn: () => getOrganizationMembers({ organizationId }),
    }),
};

const getAllEventAPI = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<Event[]>(`organizations/${organizationId}/events`);
};

export const getOrganization = ({ organizationId }: { organizationId: string }) => {
  return fetcher.get<Organization>(`organizations/${organizationId}`);
};

const getOrganizationProfile = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<OrganizationMember>(`organizations/${organizationId}/profile`);
};

export const getOrganizationPreview = (inviteCode: string) => {
  return fetcher.get<Organization>(`organizations/preview?inviteCode=${inviteCode}`);
};

export const getParticipatedOrganizations = () => {
  return fetcher.get<Organization[]>(`organizations/participated`);
};

const getOrganizationMembers = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<OrganizationMember[]>(`organizations/${organizationId}/organization-members`);
};
