import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {

        String dbHost = "localhost";
        int dbPort = 5432;
        String dbUser = "postgres";
        String dbPassword = "admin"; //
        String dbName = "scheduler_db";
        String dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        Connection connection = null;
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        RegularUser activeUser = null;


        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connected to the database successfully!");

            // LOGIN *********************************************************************************************************************************************************

            UserService userService = new UserService();
            boolean isAuthenticated = false;
            while(!isAuthenticated) {
                System.out.println("Do you want to (1) Register or (2) Login?");
                choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    userService.registerUser(scanner, connection);
                } else if (choice == 2) {
                    if (userService.loginUser(scanner, connection)) {
                        activeUser = userService.getCurrentUser();
                        System.out.println(activeUser.getUsername());
                        isAuthenticated = true;
                    }
                } else {
                    System.out.println("Invalid choice.");
                }
            }
            //************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************
            boolean loggedIn = true;
            while(loggedIn) {
                try{
                    boolean menuStatus = true;
                    while(menuStatus){
                System.out.println("**************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************");
                System.out.println("MENU:\n(1)Schedule a new task\n(2)Add a new tag\n(3)Schedule a new reminder\n(4)View data\n(5)Edit a task\n(6)Remove a tag\n(7)Cancel a reminder\n(8)Tag a Task\n(9)Search for a user(ADMIN!)\n(10)\n(11)Help\n(12)Logout");
                System.out.println("**************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************");

                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        activeUser.addTask();
                        break;
                    case 2:
                        activeUser.addTag();
                        break;
                    case 3:
                        activeUser.addReminder();
                        break;
                    case 4:
                        System.out.println("Which data would you like to view?\n(1)Tasks\n(2)Tags\n(3)Reminders");
                        int choiceView = scanner.nextInt();
                        scanner.nextLine();
                        switch (choiceView) {
                            case 1:
                                activeUser.displayUserTasks();
                                break;
                            case 2:
                                activeUser.displayUserTags();
                                break;
                            case 3:
                                activeUser.displayUserReminders();
                                break;
                            default:
                                System.out.println("Invalid choice");
                        }
                        break;
                    case 5:
                        activeUser.editTask();
                        break;
                    case 6:
                        activeUser.deleteTag();
                        break;
                    case 7:
                        activeUser.deleteReminder();
                        break;
                    case 8:
                        activeUser.tagATask();
                        break;
                    case 9:
                        activeUser.searchUserByEmail();
                        break;
                    case 10:
                        System.out.println("WIP");
                        break;
                    case 11:
                        System.out.println("This is a scheduling application that lets you add/remove/edit upcoming tasks.\nThe features it has: Tags\\Reminders\\User system\n\n\nAuthor: Simonas Jaunius Urbutis\nVU, MIF FACULTY, Informatics, 2nd year");
                        break;
                    case 12:
                        System.out.println("Bye bye....");
                        loggedIn = false;
                        menuStatus = false;
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;

                }
            }
                }catch (Exception e) {
                    System.out.println("An error occurred in the menu: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }finally {
            if(connection != null) {
                try {
                    connection.close();
                }catch (SQLException e) {
                    System.out.println("Failed to close the connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
