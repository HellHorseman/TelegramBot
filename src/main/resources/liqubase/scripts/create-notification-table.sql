--liquibase formatted sql

-- changeset esheffer:1
CREATE TABLE notification_task
(
task_id           BIGSERIAL PRIMARY KEY,
chat_id           BIGINT    NOT NULL,
notification_text TEXT      NOT NULL,
notification_time TIMESTAMP NOT NULL
)