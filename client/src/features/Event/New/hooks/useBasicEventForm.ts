import { useState, useMemo } from 'react';

import { BasicEventFormFields } from '../../types/Event';
import { FIELD_CONFIG } from '../constants/formFieldConfig';
import { validateEventForm } from '../utils/validateEventForm';

export const useBasicEventForm = () => {
  const [basicForm, setBasicForm] = useState<BasicEventFormFields>({
    title: '',
    eventStart: '',
    eventEnd: '',
    registrationEnd: '',
    place: '',
    description: '',
    maxCapacity: 0,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleValueChange = <K extends keyof BasicEventFormFields>(
    key: K,
    value: BasicEventFormFields[K]
  ) => {
    setBasicForm((prev) => ({ ...prev, [key]: value }));
  };

  const validateField = (key: keyof BasicEventFormFields, value: string | number) => {
    const updated = { ...basicForm, [key]: value };
    const validation = validateEventForm(updated);

    setErrors((prev) => ({ ...prev, [key]: validation[key] || '' }));
  };

  const validate = () => {
    const validation = validateEventForm(basicForm);
    setErrors(validation);
    return Object.keys(validation).length === 0;
  };

  const isValid = useMemo(() => {
    const hasNoErrors = Object.values(errors).every((value) => !value);

    const allRequiredFieldsFilled = Object.entries(basicForm).every(([key, value]) => {
      const isRequired = FIELD_CONFIG[key as keyof BasicEventFormFields]?.required;
      if (!isRequired) return true;

      if (typeof value === 'string') return value.trim() !== '';
      if (typeof value === 'number') return value > 0;

      return true;
    });

    return hasNoErrors && allRequiredFieldsFilled;
  }, [basicForm, errors]);

  return {
    basicForm,
    handleValueChange,
    validateField,
    validate,
    isValid,
    errors,
  };
};
