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

  const handleOrganizationCreateClick = () => {
    if (!thumbnail) {
      return;
    }

    mutate(
      {
        organization: {
          name: name.trim(),
          description: description.trim(),
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

  return { handleOrganizationCreateClick, handleClose, isSubmitting: isPending };
};
