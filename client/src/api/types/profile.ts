export type ProfileAPIResponse = {
  id: number;
  name: string;
  email: string;
  picture: string | null;
  groupId: number;
  groupName: string;
};

export type ProfileAPIRequest = {
  nickname: string;
  groupId: number;
};
