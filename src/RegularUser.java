import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class RegularUser {
    String currentUsername;
    int currentUserId;
    Scanner scanner;
    Connection currentConnection;

    public RegularUser(String username) {
        this.currentUsername = currentUsername;
    }

    public RegularUser(String currentUsername, int currentUserId, Scanner scanner, Connection currentConnection) throws SQLException {
        this.currentUsername = currentUsername;
        this.currentUserId = currentUserId;
        this.scanner = scanner;
        this.currentConnection = currentConnection;
    }

    public String getUsername() {
        return currentUsername;
    }

    //ADDER FUNCTIONS *******************************************************************************************************************************************************************************
    public void addTask() throws SQLException {
        System.out.println("Enter task title:");
        String title = scanner.nextLine();
        System.out.println("Enter task description:");
        String description = scanner.nextLine();
        System.out.println("Enter due date (YYYY-MM-DD):");
        String dueDate = scanner.nextLine();
        System.out.println("Enter task status(Done\\Not Done):");
        String status = scanner.nextLine();

        String sqlInsert = "INSERT INTO scheduler.tasks (user_id, title, description, due_date, status) VALUES (?, ?, ?, ?, ?)";
        currentConnection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlInsert)) {
            preparedStatement.setInt(1, currentUserId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, description);
            preparedStatement.setDate(4, java.sql.Date.valueOf(dueDate));
            preparedStatement.setString(5, status);
            preparedStatement.executeUpdate();
            currentConnection.commit();
            rollbackOption();
            System.out.println("Task added successfully.");
        } catch(SQLException e) {
            currentConnection.rollback();
            throw e;
        }finally {
            currentConnection.setAutoCommit(true);
        }
    }

    public void addTag() throws SQLException {
        System.out.println("Enter tag name:");
        String tagName = scanner.nextLine();

        String sqlInsert = "INSERT INTO scheduler.tags (tag_name) VALUES (?)";
        currentConnection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlInsert)) {
            preparedStatement.setString(1, tagName);
            preparedStatement.executeUpdate();
            System.out.println("Tag added successfully.");
            currentConnection.commit();
            rollbackOption();
        }catch (SQLException e) {
            currentConnection.rollback();
            throw e;
        }finally {
            currentConnection.setAutoCommit(true);
        }
    }

    public void addReminder() throws SQLException {
        displayUserTasks();
        System.out.println("Enter task ID:");
        int taskId = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter reminder time (YYYY-MM-DD HH:MM:SS):");
        String reminderTime = scanner.nextLine();

        String sqlInsert = "INSERT INTO scheduler.reminders (task_id, reminder_time) VALUES (?, ?)";
        currentConnection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlInsert)) {
            preparedStatement.setInt(1, taskId);
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(reminderTime));
            preparedStatement.executeUpdate();
            currentConnection.commit();
            rollbackOption();
            System.out.println("Reminder added successfully.");
        } catch(SQLException e) {
            currentConnection.rollback();
            throw e;
        }finally {
            currentConnection.setAutoCommit(true);
        }
    }

    //Display FUNCTIONS *******************************************************************************************************************************************************************************
    public void displayUserTasks() throws SQLException {
        String sqlSelect = "SELECT task_id, title, description, due_date, status FROM scheduler.tasks WHERE user_id = ?";
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, currentUserId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Your Tasks:");
                while (resultSet.next()) {
                    int taskId = resultSet.getInt("task_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    Date dueDate = resultSet.getDate("due_date");
                    String status = resultSet.getString("status");
                    System.out.printf("Task ID: %d, Title: %s, Description: %s, Due Date: %s, Status: %s%n",
                            taskId, title, description, dueDate, status);
                }
            }
        }
    }

    public void displayUserTags() throws SQLException {
        String sqlSelect = "SELECT tag_id, tag_name FROM scheduler.tags";
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlSelect)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Available tags:");
                while (resultSet.next()) {
                    int tagId = resultSet.getInt("tag_id");
                    String tagName = resultSet.getString("tag_name");
                    System.out.printf("Tag ID: %d, Tag Name: %s%n", tagId, tagName);
                }
            }
        }
    }

    public void displayUserReminders() throws SQLException {
        String sqlSelect = "SELECT r.reminder_id, r.task_id, r.reminder_time FROM scheduler.reminders r " +
                "JOIN scheduler.tasks tk ON r.task_id = tk.task_id " +
                "WHERE tk.user_id = ?";
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, currentUserId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Your Reminders:");
                while (resultSet.next()) {
                    int reminderId = resultSet.getInt("reminder_id");
                    int taskId = resultSet.getInt("task_id");
                    java.sql.Timestamp reminderTime = resultSet.getTimestamp("reminder_time");
                    System.out.printf("Reminder ID: %d, Task ID: %d, Reminder Time: %s%n", reminderId, taskId, reminderTime);
                }
            }
        }
    }

    //EDIT FUNCTIONS *******************************************************************************************************************************************************************************
    public void editTask() throws SQLException {
        displayUserTasks();
        System.out.println("Enter task ID to edit:");
        int taskId = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the column to edit (title, description, due_date, status):");
        String column = scanner.nextLine();
        System.out.println("Enter the new value:");
        String newValue = scanner.nextLine();

        String sqlUpdate = "UPDATE scheduler.tasks SET " + column + " = ? WHERE task_id = ? AND user_id = ?";
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlUpdate)) {
            if (column.equalsIgnoreCase("due_date")) {
                preparedStatement.setDate(1, java.sql.Date.valueOf(newValue));
            } else {
                preparedStatement.setString(1, newValue);
            }
            preparedStatement.setInt(2, taskId);
            preparedStatement.setInt(3, currentUserId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task updated successfully.");
            } else {
                System.out.println("Task update failed or no changes made.");
            }
        }
    }


//DELETE FUNCTIONS *******************************************************************************************************************************************************************************

    public void deleteTask() throws SQLException {
        displayUserTasks();

        System.out.println("Enter task ID to delete:");
        int taskId = scanner.nextInt();
        scanner.nextLine();

        String sqlDelete = "DELETE FROM scheduler.tasks WHERE task_id = ? AND user_id = ?";
        currentConnection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlDelete)) {
            preparedStatement.setInt(1, taskId);
            preparedStatement.setInt(2, currentUserId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task deleted successfully.");
                currentConnection.commit();
                rollbackOption();
            } else {
                System.out.println("Task deletion failed or task not found.");
            }
        } catch (SQLException e) {
            currentConnection.rollback();
            throw e;
        } finally {
            currentConnection.setAutoCommit(true);
        }
    }

    public void deleteTag() throws SQLException {
        displayUserTags();
        System.out.println("Enter tag ID to delete:");
        int tagId = scanner.nextInt();
        scanner.nextLine();

        String sqlDelete = "DELETE FROM scheduler.tags WHERE tag_id = ?";
        currentConnection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlDelete)) {
            preparedStatement.setInt(1, tagId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tag deleted successfully.");
                rollbackOption();
            } else {
                System.out.println("Tag deletion failed or tag not found.");
            }
        } catch (SQLException e) {
            currentConnection.rollback();
            throw e;
        } finally {
            currentConnection.setAutoCommit(true);
        }
    }

    public void deleteReminder() throws SQLException {
        displayUserReminders();
        System.out.println("Enter reminder ID to delete:");
        int reminderId = scanner.nextInt();
        scanner.nextLine();

        String sqlDelete = "DELETE FROM scheduler.reminders WHERE reminder_id = ?";
        currentConnection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlDelete)) {
            preparedStatement.setInt(1, reminderId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Reminder deleted successfully.");
                currentConnection.commit();
                rollbackOption();
            } else {
                System.out.println("Reminder deletion failed or reminder not found.");
            }
        } catch (SQLException e) {
            currentConnection.rollback();
            throw e;
        } finally {
            currentConnection.setAutoCommit(true);
        }
    }

//SEARCH FUNCTIONS **********************************************************************************************************************************************************************************************************************************************************************************************************************

    public void searchUserByEmail() throws SQLException {
        System.out.println("Enter email address to search:");
        String email = scanner.nextLine();

        String sqlUserSearch = "SELECT user_id, username, email FROM scheduler.users WHERE email = ?";
        try (PreparedStatement userSearchStmt = currentConnection.prepareStatement(sqlUserSearch)) {
            userSearchStmt.setString(1, email);
            try (ResultSet userResultSet = userSearchStmt.executeQuery()) {
                if (userResultSet.next()) {
                    int userId = userResultSet.getInt("user_id");
                    String username = userResultSet.getString("username");
                    String userEmail = userResultSet.getString("email");
                    System.out.printf("User ID: %d, Username: %s, Email: %s%n", userId, username, userEmail);
                } else {
                    System.out.println("User not found.");
                }
            }
        }
    }


    //MISC FUNCTIONS ************************************************************************************************************************************************************
    public void tagATask() throws SQLException {
        displayUserTasks();
        displayUserTags();

        System.out.println("Enter task ID to tag:");
        int taskId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter tag ID to add:");
        int tagId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String sqlInsert = "INSERT INTO scheduler.tasktags (task_id, tag_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = currentConnection.prepareStatement(sqlInsert)) {
            preparedStatement.setInt(1, taskId);
            preparedStatement.setInt(2, tagId);
            preparedStatement.executeUpdate();
            System.out.println("Tag added to task successfully.");
        }
    }


    public void rollbackOption() throws SQLException {
        System.out.println("Do you wish to keep the change? YES(1)\\NO(2)");
        int choice;
        choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                currentConnection.setAutoCommit(true);
                break;
            case 2:
                currentConnection.rollback();
                System.out.println("Change undone!");
                currentConnection.setAutoCommit(true);
        }
    }
}