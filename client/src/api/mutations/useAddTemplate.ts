import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryKeys } from '../queries/event';
import { TemplateAPIRequest } from '../types/event';

export const addTemplate = async (data: TemplateAPIRequest) => {
  return await fetcher.post(`templates`, data);
};

export const useAddTemplate = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: TemplateAPIRequest) => addTemplate(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [...eventQueryKeys.templateList()] });
    },
  });
};
