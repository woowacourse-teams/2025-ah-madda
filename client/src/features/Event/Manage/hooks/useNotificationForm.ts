import { ChangeEvent, useState } from 'react';

export const useNotificationForm = () => {
  const [content, setContent] = useState('');

  const handleContentChange = (event: ChangeEvent<HTMLInputElement>) => {
    setContent(event.target.value);
  };

  const resetContent = () => {
    setContent('');
  };

  return {
    content,
    handleContentChange,
    resetContent,
  };
};
