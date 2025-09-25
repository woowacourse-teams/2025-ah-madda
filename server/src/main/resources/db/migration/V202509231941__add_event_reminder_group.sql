create table event_reminder_group
(
    event_reminder_group_id bigint auto_increment
        primary key,
    created_at              datetime(6) null,
    updated_at              datetime(6) null,
    deleted_at              datetime(6) null,
    event_id                bigint      not null,
    organization_group_id   bigint      not null,
    constraint FK_event_reminder_group__event__event_id
        foreign key (event_id) references event (event_id),
    constraint FK_event_reminder_group__org_group__org_group_id
        foreign key (organization_group_id) references organization_group (organization_group_id)
);
