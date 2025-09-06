// utils/eventButtonState.ts
export type EventButtonState = {
  text: string;
  color: 'primary' | 'tertiary';
  disabled: boolean;
  action: 'participate' | 'cancel' | 'none';
};

export const getEventButtonState = ({
  registrationEnd,
  isGuest,
  isRequiredAnswerComplete,
}: {
  registrationEnd: string;
  isGuest: boolean;
  isRequiredAnswerComplete: boolean;
}): EventButtonState => {
  const now = new Date();
  const isBeforeDeadline = now <= new Date(registrationEnd);

  if (!isBeforeDeadline) {
    return {
      text: isGuest ? '신청 완료' : '신청 마감',
      color: 'tertiary',
      disabled: true,
      action: 'none',
    };
  }

  if (isGuest) {
    return {
      text: '신청 취소',
      color: 'primary',
      disabled: false,
      action: 'cancel',
    };
  }

  return {
    text: '신청 하기',
    color: 'primary',
    disabled: !isRequiredAnswerComplete,
    action: 'participate',
  };
};
