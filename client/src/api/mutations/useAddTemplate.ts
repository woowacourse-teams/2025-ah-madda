import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { TemplateAPIRequest } from '../types/event';

export const addTemplate = async (data: TemplateAPIRequest) => {
  return await fetcher.post(`templates`, data);
};

export const useAddTemplate = () => {
  return useMutation({ mutationFn: (data: TemplateAPIRequest) => addTemplate(data) });
};
