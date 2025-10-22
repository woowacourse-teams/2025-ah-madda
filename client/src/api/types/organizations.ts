export type OrganizationAPIResponse = {
  organizationId: number;
  name: string;
  description: string;
  imageUrl: string;
};

export type InviteCodeAPIResponse = {
  inviteCode: string;
  expiresAt: string;
};

export type OrganizationParticipateAPIRequest = {
  inviteCode: string;
};

export type OrganizationMember = {
  organizationMemberId: number;
  nickname: string;
  isAdmin: boolean;
  group: OrganizationGroupAPIResponse;
};

export type CreateOrganizationAPIRequest = {
  organization: {
    name: string;
    description: string;
    nickname: string;
    groupId: number;
  };
  thumbnail: File;
};

export type CreateOrganizationAPIResponse = { organizationId: number };

export type OrganizationRole = 'ADMIN' | 'USER';

export type UpdateOrganizationMemberRolesAPIRequest = {
  organizationId: number;
  payload: {
    organizationMemberIds: number[];
    role: OrganizationRole;
  };
};

export type OrganizationGroupAPIResponse = {
  groupId: number;
  name: string;
};

export type OrganizationJoinedStatusAPIResponse = {
  isMember: boolean;
};
