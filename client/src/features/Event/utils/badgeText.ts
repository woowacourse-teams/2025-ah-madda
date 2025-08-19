export const badgeText = (registrationEnd: string) => {
  if (new Date() < new Date(registrationEnd)) {
    return { color: 'blue', text: '모집중' };
  }

  return { color: 'red', text: '신청마감' };
};
