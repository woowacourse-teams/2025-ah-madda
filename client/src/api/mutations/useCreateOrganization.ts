import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import {
  CreateOrganizationAPIRequest,
  CreateOrganizationAPIResponse,
} from '../types/organizations';

const createOrganization = async (
  body: CreateOrganizationAPIRequest
): Promise<CreateOrganizationAPIResponse> => {
  const formData = new FormData();

  const organization = {
    name: body.organization.name.trim(),
    description: body.organization.description.trim(),
    nickname: body.organization.nickname.trim(),
    groupId: body.organization.groupId,
  };

  formData.append(
    'organization',
    new Blob([JSON.stringify(organization)], { type: 'application/json' })
  );

  if (body.thumbnail) {
    formData.append('thumbnail', body.thumbnail);
  }

  return fetcher.post<CreateOrganizationAPIResponse>('organizations', formData);
};

export const useCreateOrganization = () =>
  useMutation<CreateOrganizationAPIResponse, Error, CreateOrganizationAPIRequest>({
    mutationFn: createOrganization,
  });
