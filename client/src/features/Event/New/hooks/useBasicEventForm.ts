import { useState, useMemo } from 'react';

import { BasicEventFormFields } from '../../types/Event';
import { UNLIMITED_CAPACITY } from '../constants/errorMessages';
import { FIELD_CONFIG } from '../constants/formFieldConfig';
import { validateEventForm } from '../utils/validateEventForm';

export const useBasicEventForm = () => {
  const [basicEventForm, setBasicEventForm] = useState<BasicEventFormFields>({
    title: '',
    eventStart: '',
    eventEnd: '',
    registrationEnd: '',
    place: '',
    description: '',
    maxCapacity: UNLIMITED_CAPACITY,
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

    setErrors((prev) => ({
      ...prev,
      [key]: validation[key] || '',
    }));
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

  return {
    basicEventForm,
    handleValueChange,
    validateField,
    handleChange,
    isValid,
    errors,
  };
};
