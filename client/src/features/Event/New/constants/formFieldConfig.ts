import { BasicEventFormFields } from '../../types/Event';

type FieldConfig = {
  required: boolean;
  type: 'string' | 'number' | 'number[]';
};

export const FIELD_CONFIG: Record<keyof BasicEventFormFields, FieldConfig> = {
  title: { required: true, type: 'string' },
  description: { required: false, type: 'string' },
  place: { required: false, type: 'string' },
  maxCapacity: { required: false, type: 'number' },
  eventStart: { required: true, type: 'string' },
  eventEnd: { required: true, type: 'string' },
  registrationEnd: { required: true, type: 'string' },
  eventOrganizerIds: { required: false, type: 'number[]' },
  groupIds: { required: false, type: 'number[]' },
};
