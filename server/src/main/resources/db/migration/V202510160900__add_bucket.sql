create table bucket
(
    bucket_id   bigint auto_increment
        primary key,
    state       blob         not null,
    expires_at  bigint       not null
);
