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

export const trackCreateEvent = () => {
  event({
    action: 'click_create_event',
    category: 'engagement',
    label: '이벤트 생성',
  });
};

export const trackLoadTemplate = (templateId: number) => {
  event({
    action: 'load_template',
    category: 'engagement',
    label: `템플릿 ${templateId} 불러오기`,
  });
};
