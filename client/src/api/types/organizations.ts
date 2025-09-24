export type InviteCodeAPIResponse = {
  inviteCode: string;
  expiresAt: string;
};

export type OrganizationParticipateAPIRequest = {
  nickname: string;
  groupId: number;
  inviteCode: string;
};

export type OrganizationGroup = {
  groupId: number;
  name: string;
};

export type OrganizationMember = {
  organizationMemberId: number;
  nickname: string;
  isAdmin: boolean;
  group: OrganizationGroup;
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

export type OrganizationGroupAPIResponse = {
  groupId: number;
  name: string;
};
