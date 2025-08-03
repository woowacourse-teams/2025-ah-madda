import { useState, useMemo, useEffect } from 'react';

import { CreateEventAPIRequest } from '../../types/Event';
import { FIELD_CONFIG } from '../constants/formFieldConfig';
import { validateEventForm } from '../utils/validateEventForm';

export const useBasicEventForm = () => {
  const [basicForm, setBasicForm] = useState<CreateEventAPIRequest>({
    title: '',
    eventStart: '',
    eventEnd: '',
    registrationEnd: '',
    place: '',
    description: '',
    maxCapacity: 0,
    questions: [],
  });
  const [touchedMap, setTouchedMap] = useState<
    Partial<Record<keyof CreateEventAPIRequest, boolean>>
  >({});
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    const validation = validateEventForm(basicForm);
    setErrors(validation);
  }, [basicForm]);

  const setTouched = (key: keyof CreateEventAPIRequest) => {
    setTouchedMap((prev) => ({ ...prev, [key]: true }));
  };

  const setValue = <K extends keyof CreateEventAPIRequest>(
    key: K,
    value: CreateEventAPIRequest[K]
  ) => {
    setBasicForm((prev) => ({ ...prev, [key]: value }));

    const config = FIELD_CONFIG[key];
    if (config?.required && typeof value === 'string' && value.trim() === '') {
      setTouchedMap((prev) => ({ ...prev, [key]: true }));
    }
  };

  const validate = () => {
    const validation = validateEventForm(basicForm);
    setErrors(validation);
    return Object.keys(validation).length === 0;
  };

  const validateField = (key: keyof CreateEventAPIRequest, value: string | number) => {
    const updated = { ...basicForm, [key]: value };
    const validation = validateEventForm(updated);

    setErrors((prev) => ({ ...prev, [key]: validation[key] || '' }));
  };

  const isValid = useMemo(() => Object.keys(errors).every((k) => !errors[k]), [errors]);

  return {
    basicForm,
    setValue,
    setTouched,
    validate,
    validateField,
    isValid,
    errors,
    touchedMap,
  };
};
