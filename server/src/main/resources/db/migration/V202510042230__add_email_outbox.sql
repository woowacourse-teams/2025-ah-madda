create table email_outbox
(
    email_outbox_id bigint auto_increment
        primary key,
    event_id        bigint       not null,
    recipient_email varchar(255) not null,
    content         longtext     not null,
    status          varchar(50)  not null,
    created_at      datetime(6)  not null,
    last_attempt_at datetime(6)  null,
    fail_reason     varchar(255) null,
    constraint FK_email_outbox__event__event_id
        foreign key (event_id) references event (event_id)
);
