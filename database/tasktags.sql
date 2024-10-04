CREATE TABLE IF NOT EXISTS scheduler.tasktags
(
    task_id smallint NOT NULL,
    tag_id smallint NOT NULL,


    CONSTRAINT tasktags_pkey PRIMARY KEY (tag_id, task_id),

    CONSTRAINT tag_id_fk FOREIGN KEY (tag_id)
        REFERENCES scheduler.tags (tag_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,

    CONSTRAINT task_id_fk FOREIGN KEY (task_id)
        REFERENCES scheduler.tasks (task_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;