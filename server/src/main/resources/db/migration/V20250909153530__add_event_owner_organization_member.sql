CREATE TABLE event_owner_organization_member
(
    event_owner_organization_member_id BIGINT AUTO_INCREMENT NOT NULL,
    event_id                           BIGINT                NOT NULL,
    organization_member_id             BIGINT                NOT NULL,
    created_at                         DATETIME(6)           NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                         DATETIME(6)           NULL ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at                         DATETIME(6)           NULL,

    CONSTRAINT pk_event_owner_organization_member
        PRIMARY KEY (event_owner_organization_member_id),

    CONSTRAINT fk_event_owner_organization_member_on_event
        FOREIGN KEY (event_id) REFERENCES event (event_id),

    CONSTRAINT fk_event_owner_organization_member_on_org_member
        FOREIGN KEY (organization_member_id) REFERENCES organization_member (organization_member_id),

    CONSTRAINT uk_event_owner_per_event
        UNIQUE (event_id, organization_member_id)
);
