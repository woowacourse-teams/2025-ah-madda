CREATE TABLE event_organizer
(
    event_organizer_id     BIGINT AUTO_INCREMENT NOT NULL,
    event_id               BIGINT                NOT NULL,
    organization_member_id BIGINT                NOT NULL,
    created_at             DATETIME(6)           NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at             DATETIME(6)           NULL ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at             DATETIME(6)           NULL,

    CONSTRAINT pk_event_organizer PRIMARY KEY (event_organizer_id),

    CONSTRAINT fk_event_organizer_on_event
        FOREIGN KEY (event_id) REFERENCES event (event_id),

    CONSTRAINT fk_event_organizer_on_org_member
        FOREIGN KEY (organization_member_id) REFERENCES organization_member (organization_member_id),

    CONSTRAINT uk_event_organizer_per_event
        UNIQUE (event_id, organization_member_id)
);

INSERT IGNORE INTO event_organizer (event_id,
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
