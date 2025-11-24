import { useCallback, useState } from 'react';

import { validateOrganizationForm } from '../utils/validateOrganizationForm';
import { OrgFormFields } from '../utils/validationRules';

type Options = { requireThumbnail?: boolean };

export const useOrganizationForm = (
  initial?: Partial<OrgFormFields>,
  options: Options = { requireThumbnail: true }
) => {
  const { requireThumbnail = true } = options;

  const [form, setForm] = useState<OrgFormFields>({
    name: '',
    description: '',
    thumbnail: null,
    ...initial,
  });

  const [errors, setErrors] = useState<Partial<Record<keyof OrgFormFields, string>>>({});

  const setField = <K extends keyof OrgFormFields>(key: K, value: OrgFormFields[K]) => {
    setForm((prev) => {
      const next = { ...prev, [key]: value } as OrgFormFields;
      const validation = validateOrganizationForm(next, { requireThumbnail });
      setErrors((prevErr) => ({ ...prevErr, [key]: validation[key] || '' }));
      return next;
    });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setField(name as keyof OrgFormFields, value as OrgFormFields[keyof OrgFormFields]);
  };

  const handleLogoChange = (file: File | null) => {
    setField('thumbnail', file);
  };

  const loadFormData = useCallback((data: Partial<OrgFormFields>) => {
    setForm((prev) => ({ ...prev, ...data }));
  }, []);

  const isValid = () => {
    const hasNoErrors = Object.values(errors).every((v) => !v);
    const filledName = form.name.trim().length > 0;
    const filledDesc = form.description.trim().length > 0;
    const filledThumb = requireThumbnail ? !!form.thumbnail : true;
    return hasNoErrors && filledName && filledDesc && filledThumb;
  };

  return {
    form,
    errors,
    isValid,
    handleChange,
    handleLogoChange,
    loadFormData,
  };
};
