import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { UpdateOrganizationMemberRolesAPIRequest } from '../types/organizations';

export const useUpdateOrganizationMemberRoles = () => {
  return useMutation<void, Error, UpdateOrganizationMemberRolesAPIRequest>({
    mutationFn: ({ organizationId, payload }) =>
      fetcher.patch<void>(`organizations/${organizationId}/organization-members/roles`, payload),
  });
};
