create table email_outbox
(
    email_outbox_id bigint auto_increment
        primary key,
    subject         varchar(255) not null,
    body            longtext     not null,
    locked_at       datetime(6)  null,
    created_at      datetime(6)  not null
);

create table email_outbox_recipient
(
    email_outbox_recipient_id bigint auto_increment
        primary key,
    recipient_email           varchar(255) not null,
    email_outbox_id           bigint       not null,
    constraint fk_email_outbox_recipient__email_outbox
        foreign key (email_outbox_id)
            references email_outbox (email_outbox_id)
            on delete cascade
);
