export const truncateText = (text: string, maxLength: number = 25): string => {
  return text.length > maxLength ? `${text.slice(0, maxLength)}...` : text;
};
