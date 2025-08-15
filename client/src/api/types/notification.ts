export type NotificationAPIRequest = {
  organizationMemberIds: number[];
  content: string;
};

export type PokeAPIRequest = {
  receiptOrganizationMemberId: number;
};
