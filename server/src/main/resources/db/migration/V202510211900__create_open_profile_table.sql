create table open_profile
(
    open_profile_id        bigint auto_increment
        primary key,
    created_at             datetime(6) null,
    updated_at             datetime(6) null,
    deleted_at             datetime(6) null,
    member_id              bigint      not null,
    organization_group_id  bigint      not null,
    constraint FK_open_profile__member__member_id
        foreign key (member_id) references member (member_id),
    constraint FK_open_profile__organization_group__organization_group_id
        foreign key (organization_group_id) references organization_group (organization_group_id)
);
