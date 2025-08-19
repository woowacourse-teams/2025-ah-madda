export type InviteCodeAPIResponse = {
  inviteCode: string;
  expiresAt: string;
};

export type OrganizationParticipateAPIRequest = {
  nickname: string;
  inviteCode: string;
};

export type OrganizationProfileAPIResponse = {
  organizationMemberId: number;
  nickname: string;
  isAdmin: boolean;
};
