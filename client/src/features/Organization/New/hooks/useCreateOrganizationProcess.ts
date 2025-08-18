import { useCreateOrganization } from '@/api/mutations/useCreateOrganization';

type CreateOrganizationProcessArgs = {
  name: string;
  description: string;
  thumbnail: File | null;
  onSuccess?: (organizationId: number) => void;
  onCancel?: () => void;
};

export const useCreateOrganizationProcess = ({
  name,
  description,
  thumbnail,
  onSuccess,
  onCancel,
}: CreateOrganizationProcessArgs) => {
  const { mutate, isPending } = useCreateOrganization();

  const handleCreate = (nickname: string) => {
    if (!thumbnail) {
      return;
    }

    mutate(
      {
        organization: {
          name: name.trim(),
          description: description.trim(),
          nickname: nickname.trim(),
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
    onCancel?.();
  };

  return { handleCreate, handleClose, isSubmitting: isPending };
};
