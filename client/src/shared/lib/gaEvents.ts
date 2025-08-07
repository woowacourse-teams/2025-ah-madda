import { event } from './gtag';

export const trackClickEventCard = (title: string) => {
  event({
    action: 'click_event_card',
    category: 'engagement',
    label: title,
  });
};

export const trackSendAlarm = (selectedGuestCount: number) => {
  event({
    action: 'click_send_alarm',
    category: 'notification',
    label: `${selectedGuestCount}명에게 알람 전송`,
  });
};

export const trackCreateEvent = (title: string) => {
  event({
    action: 'click_create_event',
    category: 'engagement',
    label: title,
  });
};
