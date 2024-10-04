
--active_user_tasks -> displays all of the tasks related to users
CREATE OR REPLACE VIEW scheduler.active_user_tasks AS
SELECT
    u.user_id,
    u.username,
    u.email,
    t.task_id,
    t.title
FROM
    users u
JOIN
    tasks t ON u.user_id = t.user_id
WHERE
    t.status <> 'Done';

--upcoming_reminders -> displays the reminders in the next 7 days(ones that are about to happen)
CREATE OR REPLACE VIEW scheduler.upcoming_reminders AS
SELECT
    t.title,
    r.reminder_time,
    u.username
FROM
    reminders r
JOIN 
    tasks t ON r.task_id = t.task_id
JOIN 
    users u ON t.user_id = u.user_id
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
    tasks t
GROUP BY
    t.user_id, t.status
WITH DATA;