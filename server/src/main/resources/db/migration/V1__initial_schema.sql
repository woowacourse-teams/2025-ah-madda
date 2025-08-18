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
    constraint UKmbmcqelty0fbrvxp1q58dn57t
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
    constraint FK2nyntxlhdqsw00ym24suf201k
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
    constraint FKjbkui69kscehhayjq4bgpfatm
        foreign key (member_id) references member (member_id),
    constraint FKkta3960iv2gi5rtadvyyp046g
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
    constraint FKii1bg3kyi7mnsa2qdhqo4rt4e
        foreign key (organizer_id) references organization_member (organization_member_id),
    constraint FKkarqc3c84scr3r5ncv5stqbk2
        foreign key (organization_id) references organization (organization_id)
);

create table event_notification_opt_out
(
    event_notification_opt_out_id bigint auto_increment
        primary key,
    created_at                    datetime(6) null,
    updated_at                    datetime(6) null,
    event_id                      bigint      not null,
    organization_member_id        bigint      not null,
    constraint FK9n779sgx8kyxw5n54o2iiv3wk
        foreign key (event_id) references event (event_id),
    constraint FKacgox0oodfi5khgr61xbf9prl
        foreign key (organization_member_id) references organization_member (organization_member_id)
);

create table event_statistic
(
    event_statistic_id bigint auto_increment
        primary key,
    created_at         datetime(6) null,
    updated_at         datetime(6) null,
    event_id           bigint      not null,
    constraint UKilsp4cm6nlvbu9iycpity8ggr
        unique (event_id),
    constraint FKkpjuadinndq7hefu96qhatdm6
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
    constraint FK13a3mi63066y755ahrn0yqovi
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
    constraint FKg64g944loti79u0es4r9gbbug
        foreign key (participant_id) references organization_member (organization_member_id),
    constraint FKplwm15gu4q6tj4g4ox6wkf1li
        foreign key (event_id) references event (event_id)
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
    constraint UK3ja1mxf58p6paxfcpycpblnw9
        unique (code),
    constraint FK41beg3ae7antlly33qcy4yq80
        foreign key (inviter_id) references organization_member (organization_member_id),
    constraint FKjmhv96uefmfnavwj9syrhvqks
        foreign key (organization_id) references organization (organization_id)
);

create table poke_history
(
    id           bigint auto_increment
        primary key,
    created_at   datetime(6) null,
    updated_at   datetime(6) null,
    sent_at      datetime(6) not null,
    event_id     bigint      not null,
    recipient_id bigint      not null,
    sender_id    bigint      not null,
    constraint FK288ixvd99w0ejrw7n2k4195sa
        foreign key (sender_id) references organization_member (organization_member_id),
    constraint FK3ggc3y9vkqk6uovaeaf65htwc
        foreign key (recipient_id) references organization_member (organization_member_id),
    constraint FKai1a607ofdo3u2sqkyoca2kov
        foreign key (event_id) references event (event_id)
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
    constraint FK8frr4bcabmmeyyu60qt7iiblo
        foreign key (question_id) references question (question_id),
    constraint FKlgsn4bvrvhl1xs9iw9fcghgdp
        foreign key (guest_id) references guest (guest_id)
);

create table event_questions
(
    event_event_id        bigint not null,
    questions_question_id bigint not null,
    constraint UK2owmcun4n0seoad4g8a0i0i8q
        unique (questions_question_id),
    constraint FKamfavdgoctv1guhw2tket7eht
        foreign key (questions_question_id) references question (question_id),
    constraint FKpnhlelf441atvygpctnd19128
        foreign key (event_event_id) references event (event_id)
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
    constraint FK7vahkq7bx3evaplx5bpouv12b
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
    constraint FKerx7p9tj98v0aedt3k9rpi8qs
        foreign key (organization_member_id) references organization_member (organization_member_id),
    constraint FKnu6pfe5oet9mdj15pkywvmjjt
        foreign key (reminder_history_id) references reminder_history (reminder_history_id)
);
