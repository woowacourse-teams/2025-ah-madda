export const removeNewline = (text: string) => {
  return text.replace(/\s+/g, ' ').trim();
};
