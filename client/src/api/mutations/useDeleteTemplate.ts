import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '@/api/fetcher';

import { eventQueryKeys } from '../queries/event';

const deleteTemplate = async (templateId: number) => {
  await fetcher.delete(`templates/${templateId}`);
};

export const useDeleteTemplate = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (templateId: number) => deleteTemplate(templateId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [...eventQueryKeys.templateList()] });
    },
  });
};
