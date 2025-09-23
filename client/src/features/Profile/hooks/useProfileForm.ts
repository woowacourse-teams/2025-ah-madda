import { ChangeEvent, useState } from 'react';

import { useEditProfile } from '@/api/mutations/useEditProfile';
import { useToast } from '@/shared/components/Toast/ToastContext';

type UseProfileFormProps = {
  organizationId: number;
  initialNickname: string;
  initialGroupID: number;
};

export const useProfileForm = ({
  organizationId,
  initialNickname,
  initialGroupID,
}: UseProfileFormProps) => {
  const { success: successToast, error: errorToast } = useToast();
  const [nickname, setNickname] = useState(initialNickname);
  const [selectGroup, setSelectGroup] = useState<number>(initialGroupID);
  const { mutate: editProfile, isPending } = useEditProfile();

  const handleNicknameChange = (e: ChangeEvent<HTMLInputElement>) => {
    setNickname(e.target.value);
  };

  const handleGroupChange = (groupId: number) => {
    setSelectGroup(groupId);
  };

  const handleSaveProfile = () => {
    editProfile(
      { organizationId, nickname, groupId: selectGroup },
      {
        onSuccess: () => {
          successToast('프로필이 성공적으로 변경되었어요.');
        },
        onError: (error) => {
          errorToast(error.message);
        },
      }
    );
  };

  const hasChanges = nickname !== initialNickname || selectGroup !== initialGroupID;

  return {
    nickname,
    selectGroup,
    handleNicknameChange,
    handleGroupChange,
    handleSaveProfile,
    hasChanges,
    isLoading: isPending,
  };
};
