export type Guest = {
  guestId: number;
  organizationMemberId: number;
  isChecked?: boolean;
  nickname: string;
};

export type NonGuest = Omit<Guest, 'guestId'>;
