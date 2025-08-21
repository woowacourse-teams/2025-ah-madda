import { useMemo, useRef, useState } from 'react';

import { BasicEventFormFields, CreateEventAPIRequest } from '../../types/Event';
import { FIELD_CONFIG } from '../constants/formFieldConfig';
import { computeEverNonEmpty, isProvided } from '../utils/fieldPresence';
import { validateEventForm } from '../utils/validateEventForm';

const makeInitialForm = (initialData?: Partial<CreateEventAPIRequest>): BasicEventFormFields => ({
  title: '',
  eventStart: '',
  eventEnd: '',
  registrationEnd: '',
  place: '',
  description: '',
  maxCapacity: 10,
  ...initialData,
});

export const useBasicEventForm = (initialData?: Partial<CreateEventAPIRequest>) => {
  const initialForm = makeInitialForm(initialData);

  const [basicEventForm, setBasicEventForm] = useState<BasicEventFormFields>(initialForm);
  const [errors, setErrors] = useState<Partial<Record<keyof BasicEventFormFields, string>>>({});

  const everNonEmptyRef = useRef<Partial<Record<keyof BasicEventFormFields, boolean>>>(
    computeEverNonEmpty(initialForm)
  );

  const updateAndValidate = (patch: Partial<BasicEventFormFields>) => {
    setBasicEventForm((prev) => {
      const next = { ...prev, ...patch };

      const all = validateEventForm(next);

      (Object.keys(patch) as (keyof BasicEventFormFields)[]).forEach((field) => {
        if (isProvided(next[field])) everNonEmptyRef.current[field] = true;
      });

      const filtered: Partial<Record<keyof BasicEventFormFields, string>> = {};
      (Object.keys(all) as (keyof BasicEventFormFields)[]).forEach((field) => {
        const msg = all[field];
        if (!msg) return;

        if (!isProvided(next[field]) && !everNonEmptyRef.current[field]) return;
        filtered[field] = msg;
      });

      setErrors(filtered);
      return next;
    });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    updateAndValidate({
      [name]: type === 'number' ? Number(value) : value,
    } as Partial<BasicEventFormFields>);
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
    updateAndValidate(data as Partial<BasicEventFormFields>);
  };

  return {
    basicEventForm,
    updateAndValidate,
    handleChange,
    isValid,
    errors,
    loadFormData,
  };
};
