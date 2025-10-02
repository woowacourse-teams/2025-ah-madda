create index idx_event_id__org__event_end
    on event (organization_id, event_end DESC, event_id DESC);
