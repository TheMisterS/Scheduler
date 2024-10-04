INSERT INTO users(password_hash, email, username)
VALUES
('187f54b2bb1cb1d1842c63db3651f8ce02fc2edb0c7779048879d5ae643ed8c7', 'Bob@gmail.com', 'Bob'),
('0a4daf65c931271bef454793c7b0d84df760903ef3bfa9d4886a0ce50d18b2c7', 'Tom@gmail.com', 'Tom'),
('44e60f7d88c138a9d890b9c3509b26db2d88f257718bdbb5c755402b70469014', 'Ted@gmail.com', 'Ted'),
('ddfea9f977aa805b6ad0dc6d574adea4291bf4db24395df02027f485e8051088', 'Tim@gmail.com', 'Tim');
 
INSERT INTO tasks(user_id, description, due_date, status, title)
VALUES
(1001, 'Wash the car', (CURRENT_DATE + INTERVAL '3' DAY), 'Not done', 'morning_chore'),
(1001, 'Make a healthy meal', (CURRENT_DATE + INTERVAL '2' DAY), 'Not done', 'afternoon_chore'),
(1002, 'Go to the gym', (CURRENT_DATE + INTERVAL '1' DAY), 'Not done', 'sunday_morning'),
(1003, 'Have brunch with Bob', (CURRENT_DATE), 'Done', 'bob_brunch');
 
INSERT INTO tags(tag_name)
VALUES
('Very important'),
('Semi important'),
('Not important');
 
INSERT INTO reminders(task_id, reminder_time)
VALUES
(5001, (TIMESTAMP '2024-10-19 10:23:54')),
(5001, (NOW() + INTERVAL '1' DAY + INTERVAL'5' HOUR)),
(5002, (NOW() + INTERVAL '4' DAY + INTERVAL'6' HOUR)),
(5003, (NOW() + INTERVAL '10' DAY + INTERVAL'1' HOUR));
 
INSERT INTO tasktags(task_id, tag_id)
VALUES
(5001, 2002),
(5001, 2001),
(5003, 2002),
(5002, 2000),
(5000, 2002);