ALTER TABLE question
    ADD COLUMN event_id BIGINT NOT NULL AFTER updated_at;

UPDATE question q
    JOIN event_questions eq ON q.question_id = eq.questions_question_id
    SET q.event_id = eq.event_event_id;

ALTER TABLE question
    ADD CONSTRAINT FK_question__event__event_id
        FOREIGN KEY (event_id) REFERENCES event (event_id);

DROP TABLE event_questions;
