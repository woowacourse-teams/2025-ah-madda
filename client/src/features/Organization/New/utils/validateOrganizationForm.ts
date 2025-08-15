import { getOrgErrorMessage } from './getOrgErrorMessage';
import { OrgFormFields } from './validationRules';

export const validateOrganizationForm = (
  form: OrgFormFields
): Partial<Record<keyof OrgFormFields, string>> => {
  const errors: Partial<Record<keyof OrgFormFields, string>> = {};

  (Object.keys(form) as Array<keyof OrgFormFields>).forEach((key) => {
    const msg = getOrgErrorMessage(key, form[key]);
    if (msg) errors[key] = msg;
  });

  return errors;
};
