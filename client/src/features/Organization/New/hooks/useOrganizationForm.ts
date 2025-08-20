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

  const setField = <K extends keyof OrgFormFields>(key: K, value: OrgFormFields[K]) => {
    setForm((prev) => {
      const next = { ...prev, [key]: value } as OrgFormFields;
      const validation = validateOrganizationForm(next);
      setErrors((prevErr) => ({ ...prevErr, [key]: validation[key] || '' }));
      return next;
    });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setField(name as keyof OrgFormFields, value as any);
  };

  const handleLogoChange = (file: File | null) => {
    setField('logo', file);
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
  };
};
