import { getOrgErrorMessage } from './getOrgErrorMessage';
import { OrgFormFields } from './validationRules';

type ValidateOptions = {
  requireThumbnail?: boolean;
};

export const validateOrganizationForm = (
  form: OrgFormFields,
  { requireThumbnail = true }: ValidateOptions = {}
): Partial<Record<keyof OrgFormFields, string>> => {
  const errors: Partial<Record<keyof OrgFormFields, string>> = {};

  (Object.keys(form) as Array<keyof OrgFormFields>).forEach((key) => {
    if (key === 'thumbnail' && !requireThumbnail) return;

    const msg = getOrgErrorMessage(key, form[key]);
    if (msg) errors[key] = msg;
  });

  return errors;
};
