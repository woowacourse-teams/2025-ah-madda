import { useState, useMemo } from 'react';

import { BasicEventFormFields, CreateEventAPIRequest } from '../../types/Event';
import { FIELD_CONFIG } from '../constants/formFieldConfig';
import { validateEventForm } from '../utils/validateEventForm';

export const useBasicEventForm = (initialData?: Partial<CreateEventAPIRequest>) => {
  const [basicEventForm, setBasicEventForm] = useState<BasicEventFormFields>({
    title: '',
    eventStart: '',
    eventEnd: '',
    registrationEnd: '',
    place: '',
    description: '',
    maxCapacity: 10,
    ...initialData,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleValueChange = (key: keyof BasicEventFormFields, value: string | number) => {
    setBasicEventForm((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  const validateField = (key: keyof BasicEventFormFields, value: string | number) => {
    const updated = { ...basicEventForm, [key]: value };
    const validation = validateEventForm(updated);
    setErrors(validation);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    const parsedValue = type === 'number' ? Number(value) : value;
    const key = name as keyof BasicEventFormFields;

    handleValueChange(key, parsedValue);
    validateField(key, parsedValue);
  };

  const isValid = useMemo(() => {
    const hasNoErrors = Object.values(errors).every((value) => !value);

    const allRequiredFieldsFilled = Object.entries(basicEventForm).every(([key, value]) => {
      const isRequired = FIELD_CONFIG[key as keyof BasicEventFormFields]?.required;
      if (!isRequired) return true;

      if (typeof value === 'string') return value.trim() !== '';
      if (typeof value === 'number') return value > 0;

      return true;
    });

    return hasNoErrors && allRequiredFieldsFilled;
  }, [basicEventForm, errors]);

  const loadFormData = (data: Partial<CreateEventAPIRequest>) => {
    setBasicEventForm((prev) => {
      const merged = { ...prev, ...data } as BasicEventFormFields;
      const validation = validateEventForm(merged);
      setErrors(validation);
      return merged;
    });
  };

  return {
    basicEventForm,
    handleValueChange,
    validateField,
    handleChange,
    isValid,
    errors,
    loadFormData,
  };
};
