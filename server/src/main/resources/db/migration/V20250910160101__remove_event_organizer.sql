INSERT IGNORE INTO event_owner_organization_member (event_id,
                                                    organization_member_id,
                                                    created_at,
                                                    updated_at)
SELECT event_id,
       organizer_id,
       created_at,
       COALESCE(updated_at, created_at)
FROM event
WHERE organizer_id IS NOT NULL;


ALTER TABLE event
    DROP FOREIGN KEY FK_event__org_member__organizer_id;


ALTER TABLE event
    DROP COLUMN organizer_id;
