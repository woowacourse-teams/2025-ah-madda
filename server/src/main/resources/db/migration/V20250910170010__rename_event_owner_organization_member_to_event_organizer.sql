CREATE TABLE IF NOT EXISTS event_organizer
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

    CONSTRAINT uk_event_organizer_event_id_organization_member_id
        UNIQUE (event_id, organization_member_id, deleted_at)
);

INSERT IGNORE INTO event_organizer (event_id,
                                    organization_member_id,
                                    created_at,
                                    updated_at,
                                    deleted_at)
SELECT event_id,
       organization_member_id,
       created_at,
       updated_at,
       deleted_at
FROM event_owner_organization_member;

DROP TABLE IF EXISTS event_owner_organization_member;

