import { BasicEventFormFields } from '../../types/Event';

type FieldConfig = {
  required?: boolean;
  type: 'string' | 'number';
};

export const FIELD_CONFIG: Record<keyof BasicEventFormFields, FieldConfig> = {
  title: { required: true, type: 'string' },
  description: { type: 'string' },
  place: { type: 'string' },
  maxCapacity: { type: 'number' },
  eventStart: { required: true, type: 'string' },
  eventEnd: { required: true, type: 'string' },
  registrationEnd: { required: true, type: 'string' },
};
