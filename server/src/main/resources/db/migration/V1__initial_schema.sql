create table fcm_registration_token
(
    fcm_registration_token_id bigint auto_increment
        primary key,
    member_id                 bigint       not null,
    registration_token        varchar(255) not null,
    time_stamp                datetime(6)  not null
);

create table member
(
    member_id         bigint auto_increment
        primary key,
    created_at        datetime(6)  null,
    updated_at        datetime(6)  null,
    email             varchar(255) not null,
    name              varchar(255) not null,
    profile_image_url varchar(255) not null,
    constraint UK_member__email
        unique (email)
);

create table event_template
(
    event_template_id bigint auto_increment
        primary key,
    created_at        datetime(6)  null,
    updated_at        datetime(6)  null,
    description       varchar(255) not null,
    title             varchar(255) not null,
    member_id         bigint       not null,
    constraint FK_event_template__member__member_id
        foreign key (member_id) references member (member_id)
);

create table organization
(
    organization_id bigint auto_increment
        primary key,
    created_at      datetime(6)  null,
    updated_at      datetime(6)  null,
    description     varchar(255) not null,
    image_url       varchar(255) not null,
    name            varchar(255) not null
);

create table organization_member
(
    organization_member_id bigint auto_increment
        primary key,
    created_at             datetime(6)            null,
    updated_at             datetime(6)            null,
    nickname               varchar(255)           not null,
    member_id              bigint                 not null,
    organization_id        bigint                 not null,
    role                   enum ('ADMIN', 'USER') not null,
    constraint FK_org_member__member__member_id
        foreign key (member_id) references member (member_id),
    constraint FK_org_member__org__org_id
        foreign key (organization_id) references organization (organization_id)
);

create table event
(
    event_id           bigint auto_increment
        primary key,
    created_at         datetime(6)  null,
    updated_at         datetime(6)  null,
    description        longtext     null,
    event_end          datetime(6)  null,
    event_start        datetime(6)  null,
    registration_end   datetime(6)  null,
    registration_start datetime(6)  null,
    max_capacity       int          not null,
    place              varchar(255) null,
    title              varchar(255) not null,
    organization_id    bigint       not null,
    organizer_id       bigint       not null,
    constraint FK_event__org__org_id
        foreign key (organization_id) references organization (organization_id),
    constraint FK_event__org_member__organizer_id
        foreign key (organizer_id) references organization_member (organization_member_id)
);

create table event_notification_opt_out
(
    event_notification_opt_out_id bigint auto_increment
        primary key,
    created_at                    datetime(6) null,
    updated_at                    datetime(6) null,
    event_id                      bigint      not null,
    organization_member_id        bigint      not null,
    constraint FK_event_notification_opt_out__event__event_id
        foreign key (event_id) references event (event_id),
    constraint FK_event_notification_opt_out__org_member__org_member_id
        foreign key (organization_member_id) references organization_member (organization_member_id)
);

create table event_statistic
(
    event_statistic_id bigint auto_increment
        primary key,
    created_at         datetime(6) null,
    updated_at         datetime(6) null,
    event_id           bigint      not null,
    constraint UK_event_statistic__event_id
        unique (event_id),
    constraint FK_event_statistic__event__event_id
        foreign key (event_id) references event (event_id)
);

create table event_view_metric
(
    event_view_metric_id bigint auto_increment
        primary key,
    created_at           datetime(6) null,
    updated_at           datetime(6) null,
    view_count           int         not null,
    view_date            date        not null,
    event_statistic_id   bigint      not null,
    constraint FK_event_view_metric__event_statistic__event_statistic_id
        foreign key (event_statistic_id) references event_statistic (event_statistic_id)
);

create table guest
(
    guest_id       bigint auto_increment
        primary key,
    created_at     datetime(6) null,
    updated_at     datetime(6) null,
    event_id       bigint      not null,
    participant_id bigint      not null,
    constraint FK_guest__event__event_id
        foreign key (event_id) references event (event_id),
    constraint FK_guest__org_member__participant_id
        foreign key (participant_id) references organization_member (organization_member_id)
);

create table invite_code
(
    invite_code_id  bigint auto_increment
        primary key,
    created_at      datetime(6)  null,
    updated_at      datetime(6)  null,
    code            varchar(255) not null,
    expires_at      datetime(6)  not null,
    inviter_id      bigint       not null,
    organization_id bigint       not null,
    constraint UK_invite_code__code
        unique (code),
    constraint FK_invite_code__org__org_id
        foreign key (organization_id) references organization (organization_id),
    constraint FK_invite_code__org_member__inviter_id
        foreign key (inviter_id) references organization_member (organization_member_id)
);

create table poke_history
(
    poke_history_id           bigint auto_increment
        primary key,
    created_at   datetime(6) null,
    updated_at   datetime(6) null,
    sent_at      datetime(6) not null,
    event_id     bigint      not null,
    recipient_id bigint      not null,
    sender_id    bigint      not null,
    constraint FK_poke_history__event__event_id
        foreign key (event_id) references event (event_id),
    constraint FK_poke_history__org_member__recipient_id
        foreign key (recipient_id) references organization_member (organization_member_id),
    constraint FK_poke_history__org_member__sender_id
        foreign key (sender_id) references organization_member (organization_member_id)
);

create table question
(
    question_id   bigint auto_increment
        primary key,
    created_at    datetime(6)  null,
    updated_at    datetime(6)  null,
    is_required   bit          not null,
    order_index   int          not null,
    question_text varchar(255) not null
);

create table answer
(
    answer_id   bigint auto_increment
        primary key,
    created_at  datetime(6)  null,
    updated_at  datetime(6)  null,
    answer_text varchar(255) not null,
    guest_id    bigint       not null,
    question_id bigint       not null,
    constraint FK_answer__guest__guest_id
        foreign key (guest_id) references guest (guest_id),
    constraint FK_answer__question__question_id
        foreign key (question_id) references question (question_id)
);

create table event_questions
(
    event_event_id        bigint not null,
    questions_question_id bigint not null,
    constraint UK_event_questions__questions_question_id
        unique (questions_question_id),
    constraint FK_event_questions__event__event_event_id
        foreign key (event_event_id) references event (event_id),
    constraint FK_event_questions__question__question_id
        foreign key (questions_question_id) references question (question_id)
);

create table reminder_history
(
    reminder_history_id bigint auto_increment
        primary key,
    created_at          datetime(6)  null,
    updated_at          datetime(6)  null,
    content             varchar(255) not null,
    sent_at             datetime(6)  not null,
    event_id            bigint       not null,
    constraint FK_reminder_history__event__event_id
        foreign key (event_id) references event (event_id)
);

create table reminder_recipient
(
    reminder_recipient_id  bigint auto_increment
        primary key,
    created_at             datetime(6) null,
    updated_at             datetime(6) null,
    organization_member_id bigint      not null,
    reminder_history_id    bigint      not null,
    constraint FK_reminder_recipient__org_member__org_member_id
        foreign key (organization_member_id) references organization_member (organization_member_id),
    constraint FK_reminder_recipient__reminder_history__reminder_history_id
        foreign key (reminder_history_id) references reminder_history (reminder_history_id)
);

