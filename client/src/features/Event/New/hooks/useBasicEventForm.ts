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
  eventOrganizerIds: [],
  groupIds: [],
  ...initialData,
});

type UseBasicEventFormOptions = {
  defaultCoHostId?: number;
  requireGroupSelection?: boolean;
};

export const useBasicEventForm = (
  initialData?: Partial<CreateEventAPIRequest>,
  options?: UseBasicEventFormOptions
) => {
  const initialSnapshotRef = useRef<BasicEventFormFields | null>(null);

  const [basicEventForm, setBasicEventForm] = useState<BasicEventFormFields>(() => {
    const base = makeInitialForm(initialData);
    const defaultCoHostId = options?.defaultCoHostId;

    const hasIds = Array.isArray(base.eventOrganizerIds) && base.eventOrganizerIds.length > 0;

    const init =
      defaultCoHostId != null && !hasIds ? { ...base, eventOrganizerIds: [defaultCoHostId] } : base;

    initialSnapshotRef.current = init;
    return init;
  });

  const [errors, setErrors] = useState<Partial<Record<keyof BasicEventFormFields, string>>>({});

  const everNonEmptyRef = useRef<Partial<Record<keyof BasicEventFormFields, boolean>>>(
    computeEverNonEmpty(initialSnapshotRef.current as BasicEventFormFields)
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

  const isFilled = (v: unknown) => {
    if (typeof v === 'string') return v.trim() !== '';
    if (typeof v === 'number') return v > 0;
    if (Array.isArray(v)) return v.length > 0;
    return true;
  };

  const requireGroup = options?.requireGroupSelection ?? true;

  const isValid = useMemo(() => {
    const hasNoErrors = Object.values(errors).every((value) => !value);

    const allRequiredFieldsFilled = Object.entries(basicEventForm).every(([key, value]) => {
      const isRequired = FIELD_CONFIG[key as keyof BasicEventFormFields]?.required;
      if (!isRequired) return true;
      return isFilled(value);
    });

    const hasGroup =
      !requireGroup ||
      (Array.isArray(basicEventForm.groupIds) && basicEventForm.groupIds.length > 0);

    return hasNoErrors && allRequiredFieldsFilled && hasGroup;
  }, [basicEventForm, errors, requireGroup]);

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
