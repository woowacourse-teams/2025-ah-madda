import { BasicEventFormFields, CreateEventAPIRequest } from '../../types/Event';
import { FIELD_CONFIG } from '../constants/formFieldConfig';

import { getErrorMessage } from './getErrorMessage';

export const validateEventForm = (
  formData: BasicEventFormFields
): Partial<Record<keyof BasicEventFormFields, string>> => {
  const newErrors: Partial<Record<keyof BasicEventFormFields, string>> = {};

  const startStr = (formData.eventStart ?? '').toString().trim();
  const endStr = (formData.eventEnd ?? '').toString().trim();
  const bothDateEmpty = !startStr && !endStr;

  (Object.keys(FIELD_CONFIG) as Array<keyof BasicEventFormFields>).forEach((key) => {
    if ((key === 'eventStart' || key === 'eventEnd') && bothDateEmpty) return;

    const msg = getErrorMessage(
      key as keyof CreateEventAPIRequest,
      String(formData[key] ?? ''),
      formData
    );
    if (msg) newErrors[key] = msg;
  });

  return newErrors;
};
