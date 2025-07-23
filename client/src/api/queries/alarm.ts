import { fetcher } from '../fetcher';

export const alarmMutationOptions = {
  alarms: (eventId: number) => ({
    mutationFn: (content: string) => postAlarm(eventId, content),
  }),
};

const postAlarm = async (eventId: number, content: string) => {
  await fetcher.post(`events/${eventId}/notify-non-guests`, {
    json: {
      content,
    },
  });
};
