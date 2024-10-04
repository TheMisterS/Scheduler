CREATE TRIGGER trigger_refresh_tasks_summary
AFTER INSERT OR UPDATE OR DELETE
ON scheduler.tasks
FOR EACH STATEMENT
EXECUTE FUNCTION refresh_tasks_summary();

CREATE TRIGGER check_due_date
BEFORE UPDATE
ON tasks
FOR EACH ROW
WHEN (OLD.due_date IS DISTINCT FROM NEW.due_date OR OLD.status IS DISTINCT FROM NEW.status)
EXECUTE FUNCTION update_task_status();

CREATE TRIGGER trigger_delete_reminders_when_done
AFTER UPDATE
ON tasks
FOR EACH ROW
EXECUTE FUNCTION delete_associated_reminders();