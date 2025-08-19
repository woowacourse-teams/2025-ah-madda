export type Organization = {
  organizationId: number;
  name: string;
  description: string;
  imageUrl: string;
};

export type OrganizationWithRole = Organization & {
  isAdmin: boolean;
};
