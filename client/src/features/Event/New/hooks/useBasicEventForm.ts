import { useState, useMemo, useEffect } from 'react';

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
  const [touchedMap, setTouchedMap] = useState<
    Partial<Record<keyof BasicEventFormFields, boolean>>
  >({});
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    const validation = validateEventForm(basicForm);
    setErrors(validation);
  }, [basicForm]);

  const setTouched = (key: keyof BasicEventFormFields) => {
    setTouchedMap((prev) => ({ ...prev, [key]: true }));
  };

  const handleValueChange = <K extends keyof BasicEventFormFields>(
    key: K,
    value: BasicEventFormFields[K]
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

  const validateField = (key: keyof BasicEventFormFields, value: string | number) => {
    const updated = { ...basicForm, [key]: value };
    const validation = validateEventForm(updated);

    setErrors((prev) => ({ ...prev, [key]: validation[key] || '' }));
  };

  const isValid = useMemo(() => Object.keys(errors).every((k) => !errors[k]), [errors]);

  return {
    basicForm,
    handleValueChange,
    setTouched,
    validate,
    validateField,
    isValid,
    errors,
    touchedMap,
  };
};
