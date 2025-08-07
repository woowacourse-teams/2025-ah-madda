import { useState } from 'react';

import { useQuery } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';

export const useTemplateLoader = () => {
  const [selectedEventId, setSelectedEventId] = useState(0);

  const { data: template } = useQuery({
    ...eventQueryOptions.template(selectedEventId),
    enabled: !!selectedEventId,
  });

  const handleSelectEvent = (eventId: number) => {
    setSelectedEventId(eventId);
  };

  return {
    selectedEventId,
    handleSelectEvent,
    template,
  };
};
