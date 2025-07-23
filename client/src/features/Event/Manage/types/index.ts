export type Guest = {
  guestId: number;
  organizationMemberId: number;
  nickname: string;
};

export type NonGuest = Omit<Guest, 'guestId'>;
