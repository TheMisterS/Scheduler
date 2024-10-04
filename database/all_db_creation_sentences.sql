
CREATE DATABASE scheduler_db WITH OWNER = postgres;

CREATE SCHEMA IF NOT EXISTS scheduler AUTHORIZATION postgres;

CREATE TABLE IF NOT EXISTS scheduler.users
(
    password_hash character varying ,
    email character varying(255) ,
    username character varying(64) ,
    user_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1000 MINVALUE 1000 MAXVALUE 32767 CACHE 1 ),


    
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_password_hash_check CHECK (password_hash ~* '^[A-Fa-f0-9]{64}$'),
    CONSTRAINT users_email_check CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT users_username_check CHECK (username ~ '^[A-Za-z0-9_]+$')
)

TABLESPACE pg_default;


CREATE TABLE IF NOT EXISTS sche duler.tags
(
    tag_id smallint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 2000 MINVALUE 2000 MAXVALUE 32767 CACHE 1 ),
    tag_name character varying(64),
    
    CONSTRAINT "Tags_pkey" PRIMARY KEY (tag_id)
)

TABLESPACE pg_default;


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

-- INDEXES

CREATE INDEX IF NOT EXISTS tasks_user_id_idx
    ON scheduler.tasks USING btree (user_id)
    TABLESPACE pg_default;

CREATE UNIQUE INDEX IF NOT EXISTS users_email_email1_idx
    ON scheduler.users
    USING btree (email)
    WITH (deduplicate_items = TRUE)
    TABLESPACE pg_default;

--FUNCTIONS

CREATE OR REPLACE FUNCTION refresh_tasks_summary()
RETURNS TRIGGER AS $$
BEGIN
    REFRESH MATERIALIZED VIEW scheduler.tasks_summary;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_task_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.due_date < CURRENT_DATE AND NEW.status <> ('Done') THEN
        NEW.status := 'Overdue';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_associated_reminders()
RETURNS TRIGGER AS $$
BEGIN
    --check if the status has been updated to 'Done'
    IF NEW.status = 'Done' AND OLD.status IS DISTINCT FROM NEW.status THEN
        --delete all reminders associated with the task
        DELETE FROM reminders WHERE task_id = NEW.task_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--TRIGGERS

CREATE TRIGGER trigger_refresh_tasks_summary
AFTER INSERT OR UPDATE OR DELETE
ON scheduler.tasks
FOR EACH STATEMENT
EXECUTE FUNCTION refresh_tasks_summary();

CREATE TRIGGER check_due_date
BEFORE UPDATE
ON scheduler.tasks
FOR EACH ROW
WHEN (OLD.due_date IS DISTINCT FROM NEW.due_date OR OLD.status IS DISTINCT FROM NEW.status)
EXECUTE FUNCTION update_task_status();

CREATE TRIGGER trigger_delete_reminders_when_done
AFTER UPDATE
ON scheduler.tasks
FOR EACH ROW
EXECUTE FUNCTION delete_associated_reminders();

--active_user_tasks -> displays all of the tasks related to users
CREATE OR REPLACE VIEW scheduler.active_user_tasks AS
SELECT
    u.user_id,
    u.username,
    u.email,
    t.task_id,
    t.title
FROM
    scheduler.users u
JOIN
    scheduler.tasks t ON u.user_id = t.user_id
WHERE
    t.status <> 'Done';

--upcoming_reminders -> displays the reminders in the next 7 days(ones that are about to happen)
CREATE OR REPLACE VIEW scheduler.upcoming_reminders AS
SELECT
    t.title,
    r.reminder_time,
    u.username
FROM
    scheduler.reminders r
JOIN 
    scheduler.tasks t ON r.task_id = t.task_id
JOIN 
    scheduler.users u ON t.user_id = u.user_id
WHERE
    r.reminder_time >= now() AND r.reminder_time <= (now() + INTERVAL '7 days');

--tasks_summary -> counts the amount of tasks user has with different statuses(Done/Not done/Overdue)
CREATE MATERIALIZED VIEW IF NOT EXISTS scheduler.tasks_summary
TABLESPACE pg_default AS
SELECT 
    t.user_id,
    t.status,
    count(*) AS task_count
FROM
    scheduler.tasks t
GROUP BY
    t.user_id, t.status
WITH DATA;