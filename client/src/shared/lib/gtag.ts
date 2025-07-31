const GA_TRACKING_ID = process.env.GOOGLE_ANALYTICS_ID;

declare global {
  // eslint-disable-next-line @typescript-eslint/consistent-type-definitions
  interface Window {
    gtag: (
      command: 'config' | 'event' | 'js',
      targetId: string | Date,
      config?: Record<string, unknown>
    ) => void;
  }
}

export const pageview = (url: string) => {
  if (typeof window !== 'undefined' && GA_TRACKING_ID && window.gtag) {
    if (GA_TRACKING_ID && window.gtag) {
      window.gtag('config', GA_TRACKING_ID, {
        page_path: url,
      });
    }
  }
};

type GTagEvent = {
  action: string;
  category?: string;
  label?: string;
  value?: string;
};

export const event = ({ action, category, label, value }: GTagEvent) => {
  if (window.gtag) {
    window.gtag('event', action, {
      event_category: category,
      event_label: label,
      value: value,
    });
  }
};
