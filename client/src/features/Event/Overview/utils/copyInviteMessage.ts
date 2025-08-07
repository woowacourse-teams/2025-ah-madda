export const copyInviteMessage = async (link: string) => {
  const message = `[조직 초대 링크]\n조직에 합류하려면 아래 링크를 클릭하세요! 🙌\n${link}`;
  try {
    await navigator.clipboard.writeText(message);
    alert(`초대 코드가 복사되었습니다.`);
  } catch {
    alert('잠시 후 다시 시도해 주세요');
  }
};
