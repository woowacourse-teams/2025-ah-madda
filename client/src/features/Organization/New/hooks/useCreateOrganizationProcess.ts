import { useModal } from '@/shared/hooks/useModal';

type Args = {
  name: string;
  description: string;
  thumbnail: File | null;
  onSuccess?: (orgId?: number) => void;
  onError?: (err: unknown) => void;
};

export const useCreateOrganizationProcess = ({
  name,
  description,
  thumbnail,
  onSuccess,
  onError,
}: Args) => {
  const { close } = useModal();

  const handleCreate = (nickname: string) => {
    const formData = buildOrganizationFormData({ name, description, nickname, thumbnail });
    // A.TODO: 조직 생성 API 호출

    onSuccess?.();
    close();
  };

  const handleClose = () => close();

  return { handleCreate, handleClose };
};
function buildOrganizationFormData(arg0: {
  name: string;
  description: string;
  nickname: string;
  thumbnail: File | null;
}) {
  throw new Error('Function not implemented.');
}
