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

  const patchAndValidate = (patch: Partial<BasicEventFormFields>) => {
    setBasicEventForm((prev) => {
      const next = { ...prev, ...patch };
      setErrors(validateEventForm(next));
      return next;
    });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    const parsedValue = type === 'number' ? Number(value) : value;
    const key = name as keyof BasicEventFormFields;

    patchAndValidate({ [key]: parsedValue } as Partial<BasicEventFormFields>);
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
    patchAndValidate(data as Partial<BasicEventFormFields>);
  };

  return {
    basicEventForm,
    patchAndValidate,
    handleChange,
    isValid,
    errors,
    loadFormData,
  };
};
