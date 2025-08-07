export const isEmpty = (value: string) => value.trim() === '';
export const isFutureDate = (value: string) => new Date(value) > new Date();
export const isRegistrationEndAfterEventStart = (registrationEnd: string, eventStart: string) =>
  new Date(registrationEnd) > new Date(eventStart);
export const isEventEndBeforeOrEqualEventStart = (eventEnd: string, eventStart: string) =>
  new Date(eventEnd) <= new Date(eventStart);
export const isPositiveInteger = (value: string) => {
  const num = Number(value);
  return Number.isInteger(num) && num > 0;
};
