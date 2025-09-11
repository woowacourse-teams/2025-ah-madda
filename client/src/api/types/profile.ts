export type Profile = {
  id: number;
  name: string;
  email: string;
  picture: string | null;
};

export type OrganizationProfile = {
  organizationMemberId: number;
  nickname: string;
  isAdmin: boolean;
};
