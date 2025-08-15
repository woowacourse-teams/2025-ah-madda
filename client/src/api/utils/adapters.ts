import { CreateOrganizationAPIRequest } from '../types/organizations';

type CreateOrganizationDTO = {
  name: string;
  description: string;
  nickName: string;
};

export function toOrganizationFormData(req: CreateOrganizationAPIRequest): FormData {
  const dto: CreateOrganizationDTO = {
    name: req.organization.name.trim(),
    description: req.organization.description.trim(),
    nickName: req.organization.nickname.trim(),
  };

  const fd = new FormData();
  fd.append('organization', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
  fd.append('thumbnail', req.thumbnail);
  return fd;
}
