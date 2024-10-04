
-- EMAIL CHECK
INSERT INTO users(password_hash, email, username)
VALUES
('8ea09b714f0cb4f901737c2a566c550d66c4f51a2e665f36a562e35e684b655f', 'example@zzz', 'tom');
 
INSERT INTO users(password_hash, email, username)
VALUES
('8ea09b714f0cb4f901737c2a566c550d66c4f51a2e665f36a562e35e684b655f', 'example@.com', 'tom');

-- DUE DATE CHECK
INSERT INTO tasks(user_id, description, due_date, status, title)
VALUES
(1001, 'example', (CURRENT_DATE - INTERVAL '3' DAY), 'Not done', 'example');

-- STATUS CHECK 
INSERT INTO tasks(user_id, description, due_date, status, title)
VALUES
(1001, 'example', CURRENT_DATE, 'BingBong', 'example');

--TRIGGER CHECK -> Materialized view

SELECT * FROM tasks_summary;

INSERT INTO tasks(user_id, description, due_date, status, title)
VALUES (1002,'trigger test description', (NOW() +INTERVAL '10' DAY), 'Done', 'trigger_test');

SELECT * FROM tasks_summary;

UPDATE tasks
SET status = 'Done'
WHERE user_id = '1002' AND status = 'Not done';

SELECT * FROM tasks_summary;


--TRIGGER CHECK -> Due_Date -> didn't figure out how check :)))), have to change the current date of NOW() or delete the constraint

--TRIGGER CHECK -> Associated reminders

SELECT * from reminders;
SELECT * from tasks;

UPDATE tasks
SET status = 'Done'
WHERE task_id = '5001';

SELECT * from reminders;
SELECT * from tasks;

