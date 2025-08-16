import { useState } from 'react';

import { validateOrganizationForm } from '../utils/validateOrganizationForm';
import { OrgFormFields } from '../utils/validationRules';

export const useOrganizationForm = (initial?: Partial<OrgFormFields>) => {
  const [form, setForm] = useState<OrgFormFields>({
    name: '',
    description: '',
    logo: null,
    ...initial,
  });

  const [errors, setErrors] = useState<Partial<Record<keyof OrgFormFields, string>>>({});

  const handleValueChange = <K extends keyof OrgFormFields>(key: K, value: OrgFormFields[K]) => {
    setForm((prev) => ({ ...prev, [key]: value }) as OrgFormFields);
  };

  const validateField = <K extends keyof OrgFormFields>(key: K, value: OrgFormFields[K]) => {
    const updated = { ...form, [key]: value } as OrgFormFields;
    const validation = validateOrganizationForm(updated);
    setErrors((prev) => ({ ...prev, [key]: validation[key] || '' }));
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    const key = name as keyof OrgFormFields;
    handleValueChange(key, value as OrgFormFields[typeof key]);
    validateField(key, value as OrgFormFields[typeof key]);
  };

  const handleLogoChange = (file: File | null) => {
    handleValueChange('logo', file);
    validateField('logo', file);
  };

  const isValid = () => {
    const hasNoErrors = Object.values(errors).every((v) => !v);
    const filled = !!form.logo && form.name.trim().length > 0 && form.description.trim().length > 0;
    return hasNoErrors && filled;
  };

  return {
    form,
    errors,
    isValid,
    handleChange,
    handleLogoChange,
    handleValueChange,
    validateField,
  };
};
