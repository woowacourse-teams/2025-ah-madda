import { ChangeEvent, useState } from 'react';

import { useEditNickname } from '@/api/mutations/useEditNickname';
import { useToast } from '@/shared/components/Toast/ToastContext';

type UseNicknameFormProps = {
  organizationId: number;
  initialNickname: string;
};

export const useNicknameForm = ({ organizationId, initialNickname }: UseNicknameFormProps) => {
  const { success: successToast, error: errorToast } = useToast();
  const [nickname, setNickname] = useState(initialNickname);
  const { mutate: editNickname, isPending } = useEditNickname();

  const handleNicknameChange = (e: ChangeEvent<HTMLInputElement>) => {
    setNickname(e.target.value);
  };

  const handleSaveNickname = () => {
    editNickname(
      { organizationId, nickname },
      {
        onSuccess: () => {
          successToast('닉네임이 성공적으로 변경되었어요.');
        },
        onError: (error) => {
          errorToast(error.message);
        },
      }
    );
  };

  const hasChanges = nickname !== initialNickname;

  return {
    nickname,
    handleNicknameChange,
    handleSaveNickname,
    hasChanges,
    isLoading: isPending,
  };
};
