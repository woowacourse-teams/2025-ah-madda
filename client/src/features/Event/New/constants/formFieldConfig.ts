import { EventFormData } from '../../types/Event';

type FieldConfig = {
  required?: boolean;
  type: 'string' | 'number';
};

export const FIELD_CONFIG: Partial<Record<keyof EventFormData, FieldConfig>> = {
  title: { required: true, type: 'string' },
  description: { required: true, type: 'string' },
  place: { required: true, type: 'string' },
  maxCapacity: { type: 'number' },
  eventStart: { required: true, type: 'string' },
  eventEnd: { required: true, type: 'string' },
  registrationEnd: { required: true, type: 'string' },
};
