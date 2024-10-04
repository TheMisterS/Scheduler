
CREATE TABLE IF NOT EXISTS scheduler.tasks
(
    task_id smallint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 5000 MINVALUE 5000 MAXVALUE 32767 CACHE 1 ),
    user_id smallint NOT NULL,
    description TEXT DEFAULT 'Please enter the task description',
    due_date date,
    status character(10) DEFAULT 'Not done',
    title character varying(64),


    CONSTRAINT tasks_pkey PRIMARY KEY (task_id),
    CONSTRAINT tasks_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES scheduler.users (user_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
        
    CONSTRAINT tasks_due_date_check CHECK (due_date >= CURRENT_DATE),
    CONSTRAINT tasks_title_check CHECK (title ~ '^[a-zA-Z0-9_]+$'),
    CONSTRAINT tasks_status_check CHECK (status = ANY (ARRAY['Done', 'Not done', 'Overdue']))
)

TABLESPACE pg_default;

-- INDEXES

CREATE INDEX IF NOT EXISTS tasks_user_id_idx
    ON scheduler.tasks USING btree (user_id)
    TABLESPACE pg_default;

--TRIGGERS

CREATE TRIGGER trigger_refresh_tasks_summary
    AFTER INSERT OR DELETE OR UPDATE 
    ON scheduler.tasks
    FOR EACH STATEMENT
    EXECUTE FUNCTION scheduler.refresh_tasks_summary();