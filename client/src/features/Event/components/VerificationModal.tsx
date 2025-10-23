import { isAuthenticated } from '@/api/auth';
import { ModalProps } from '@/shared/components/Modal/Modal';

import { InviteModal } from './InviteModal';
import { LoginModal } from './LoginModal';

type VerificationModalProps = {
  onJoinOrganization: (inviteCode: string) => void;
  onSubmit: VoidFunction;
  isMember?: boolean;
} & ModalProps;

export const VerificationModal = ({
  isOpen,
  onClose,
  onJoinOrganization,
  onSubmit,
  isMember,
}: VerificationModalProps) => {
  if (!isAuthenticated()) {
    return <LoginModal isOpen={isOpen} onClose={onClose} />;
  }

  if (!isMember) {
    return (
      <InviteModal
        isOpen={isOpen}
        onClose={onClose}
        onJoinOrganization={onJoinOrganization}
        onSubmit={onSubmit}
      />
    );
  }
  return null;
};
