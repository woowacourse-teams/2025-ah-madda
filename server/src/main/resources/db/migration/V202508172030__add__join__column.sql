DROP TABLE event_questions;

ALTER TABLE question ADD COLUMN event_id BIGINT NOT NULL;

ALTER TABLE question ADD CONSTRAINT fk_event_on_question FOREIGN KEY (event_id) REFERENCES event (event_id);
