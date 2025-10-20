import { OrganizationGroupAPIResponse } from './organizations';

export type Profile = {
  id: number;
  name: string;
  email: string;
  picture: string | null;
  group: OrganizationGroupAPIResponse;
};
