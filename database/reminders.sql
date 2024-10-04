CREATE TABLE IF NOT EXISTS scheduler.reminders
(
    reminder_id smallint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 3000 MINVALUE 3000 MAXVALUE 32767 CACHE 1 ),
    task_id smallint NOT NULL,
    reminder_time TIMESTAMP WITHOUT TIME ZONE,


    CONSTRAINT reminders_pkey PRIMARY KEY (reminder_id),
    CONSTRAINT task_id_fk FOREIGN KEY (task_id)
        REFERENCES scheduler.tasks (task_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;