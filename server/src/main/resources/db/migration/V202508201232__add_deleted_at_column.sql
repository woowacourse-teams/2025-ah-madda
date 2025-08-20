ALTER TABLE member
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE answer
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE event_notification_opt_out
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE event_statistic
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE event_template
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE event_view_metric
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE guest
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE invite_code
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE organization
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE organization_member
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE question
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE reminder_history
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE reminder_recipient
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE event
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE poke_history
    ADD COLUMN deleted_at DATETIME(6) NULL;
