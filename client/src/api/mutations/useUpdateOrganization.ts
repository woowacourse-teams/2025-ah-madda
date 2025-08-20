import { useMutation } from '@tanstack/react-query';

import { Organization } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

export async function updateOrganization(
  organizationId: number,
  body: {
    organization: { name: string; description: string };
    thumbnail?: File | null;
  }
): Promise<void> {
  const formData = new FormData();

  const organization = {
    name: body.organization.name.trim(),
    description: body.organization.description.trim(),
  };

  formData.append(
    'organization',
    new Blob([JSON.stringify(organization)], { type: 'application/json' })
  );

  if (body.thumbnail) {
    formData.append('thumbnail', body.thumbnail);
  }

  await fetcher.patch<void>(`organizations/${organizationId}`, formData);
}

export const useUpdateOrganization = () =>
  useMutation({
    mutationFn: ({
      organizationId,
      payload,
    }: {
      organizationId: number;
      payload: {
        organization: { name: string; description: string };
        thumbnail?: File | null;
      };
    }) => updateOrganization(organizationId, payload),
  });

export const getOrganizationDetailAPI = (organizationId: number) => {
  return fetcher.get<Organization>(`organizations/${organizationId}`);
};
