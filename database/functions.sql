
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