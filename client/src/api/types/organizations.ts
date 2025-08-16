export type InviteCodeAPIResponse = {
  inviteCode: string;
  expiresAt: string;
};

export type OrganizationParticipateAPIRequest = {
  nickname: string;
  inviteCode: string;
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
