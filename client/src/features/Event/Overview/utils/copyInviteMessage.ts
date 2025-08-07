export const copyInviteMessage = async (link: string) => {
  const message = `[조직 초대 링크]\n조직에 합류하려면 아래 링크를 클릭하세요! 🙌\n${link}`;
  try {
    await navigator.clipboard.writeText(message);
  } catch {
    alert('잠시 후 다시 시도해 주세요');
  }
};
