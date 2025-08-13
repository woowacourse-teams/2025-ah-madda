import { useState } from 'react';

import { useQuery } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';

export const usePastEventLoader = () => {
  const [selectedEventId, setSelectedEventId] = useState(0);

  const { data: pastEventList } = useQuery({
    ...eventQueryOptions.pastEventList(selectedEventId),
    enabled: !!selectedEventId,
  });

  const handleSelectEvent = (eventId: number) => {
    setSelectedEventId(eventId);
  };

  return {
    selectedEventId,
    handleSelectEvent,
    pastEventList,
  };
};
