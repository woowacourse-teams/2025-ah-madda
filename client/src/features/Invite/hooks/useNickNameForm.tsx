import { useState } from 'react';

export const useNickNameForm = () => {
  const [nickname, setNickname] = useState('');

  const handleNicknameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.value.length > 10) {
      alert('닉네임은 최대 10자까지 입력할 수 있습니다.');
      return;
    }

    setNickname(e.target.value);
  };

  return {
    nickname,
    handleNicknameChange,
  };
};
