import { ChangeEvent, useState } from 'react';

// import { useEditProfile } from '@/api/mutations/useEditProfile';

type UseProfileFormProps = {
  initialNickname: string;
  initialGroupID: number;
};

export const useProfileForm = ({ initialNickname, initialGroupID }: UseProfileFormProps) => {
  const [nickname, setNickname] = useState(initialNickname);
  const [selectedGroup, setSelectedGroup] = useState<number>(initialGroupID);
  // const { mutate: editProfile, isPending } = useEditProfile();

  const handleNicknameChange = (e: ChangeEvent<HTMLInputElement>) => {
    setNickname(e.target.value);
  };

  const handleGroupChange = (groupId: number) => {
    setSelectedGroup(groupId);
  };

  // const handleSaveProfile = () => {
  //   editProfile(
  //     { nickname, groupId: selectedGroup },
  //     {
  //       onSuccess: () => {
  //         successToast('프로필이 성공적으로 변경되었어요.');
  //       },
  //       onError: (error: HttpError) => {
  //         errorToast(error.message);
  //       },
  //     }
  //   );
  // };

  const hasChanges = nickname !== initialNickname || selectedGroup !== initialGroupID;

  return {
    nickname,
    selectedGroup,
    handleNicknameChange,
    handleGroupChange,
    // handleSaveProfile,
    hasChanges,
    // isLoading: isPending,
  };
};
