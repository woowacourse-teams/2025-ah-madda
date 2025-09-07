create table organization_group
(
    group_id        bigint auto_increment
        primary key,
    created_at      datetime(6)  null,
    updated_at      datetime(6)  null,
    deleted_at      datetime(6)  null,
    name            varchar(255) not null,
    organization_id bigint       not null,
    constraint FK_group__org__org_id
        foreign key (organization_id) references organization (organization_id)
);
