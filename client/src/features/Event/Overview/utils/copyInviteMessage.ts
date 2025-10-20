export const copyInviteMessage = async (link: string) => {
  const message = `[스페이스 초대 링크]\n스페이스에 합류하려면 아래 링크를 클릭하세요! 🙌\n${link}`;

  await navigator.clipboard.writeText(message);
};
