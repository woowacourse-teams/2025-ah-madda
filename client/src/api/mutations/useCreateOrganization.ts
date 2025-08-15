import { useMutation } from '@tanstack/react-query';

import { createOrganization } from '../queries/organization';
import {
  CreateOrganizationAPIRequest,
  CreateOrganizationAPIResponse,
} from '../types/organizations';

export const useCreateOrganization = () =>
  useMutation<CreateOrganizationAPIResponse, Error, CreateOrganizationAPIRequest>({
    mutationFn: createOrganization,
  });
