import { ChangeEvent, useState } from 'react';

import { useEditProfile } from '@/api/mutations/useEditProfile';
import { useToast } from '@/shared/components/Toast/ToastContext';

type UseProfileFormProps = {
  initialNickname: string;
  initialGroupID: number;
};

export const useProfileForm = ({ initialNickname, initialGroupID }: UseProfileFormProps) => {
  const { success, error } = useToast();
  const [nickname, setNickname] = useState(initialNickname);
  const [selectedGroup, setSelectedGroup] = useState<number>(initialGroupID);
  const { mutate: editProfile, isPending } = useEditProfile();

  const handleNicknameChange = (e: ChangeEvent<HTMLInputElement>) => {
    setNickname(e.target.value);
  };

  const handleGroupChange = (groupId: number) => {
    setSelectedGroup(groupId);
  };

  const handleSaveProfile = () => {
    editProfile(
      { nickname, groupId: selectedGroup },
      {
        onSuccess: () => {
          success('프로필이 성공적으로 변경되었어요.');
        },
        onError: () => {
          error('프로필 변경에 실패했어요.');
        },
      }
    );
  };

  const hasChanges = nickname !== initialNickname || selectedGroup !== initialGroupID;

  return {
    nickname,
    selectedGroup,
    handleNicknameChange,
    handleGroupChange,
    handleSaveProfile,
    hasChanges,
    isLoading: isPending,
  };
};
