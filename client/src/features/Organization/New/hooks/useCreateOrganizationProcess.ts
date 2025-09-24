import { useCreateOrganization } from '@/api/mutations/useCreateOrganization';

type CreateOrganizationProcessArgs = {
  name: string;
  description: string;
  thumbnail: File | null;
  onSuccess?: (organizationId: number) => void;
  onClose?: () => void;
};

export const useCreateOrganizationProcess = ({
  name,
  description,
  thumbnail,
  onSuccess,
  onClose,
}: CreateOrganizationProcessArgs) => {
  const { mutate, isPending } = useCreateOrganization();

  const handleCreate = (data: { nickname: string; groupId: number }) => {
    if (!thumbnail) {
      return;
    }

    mutate(
      {
        organization: {
          name: name.trim(),
          description: description.trim(),
          nickname: data.nickname.trim(),
          groupId: data.groupId,
        },
        thumbnail,
      },
      {
        onSuccess: ({ organizationId }) => {
          onSuccess?.(organizationId);
        },
      }
    );
  };

  const handleClose = () => {
    onClose?.();
  };

  return { handleCreate, handleClose, isSubmitting: isPending };
};
