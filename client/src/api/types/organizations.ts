export type InviteCodeAPIResponse = {
  inviteCode: string;
  expiresAt: string;
};

export type OrganizationParticipateAPIRequest = {
  nickname: string;
  inviteCode: string;
};

export type OrganizationMember = {
  organizationMemberId: number;
  nickname: string;
  isAdmin: boolean;
};

export type CreateOrganizationAPIRequest = {
  organization: {
    name: string;
    description: string;
    nickname: string;
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
