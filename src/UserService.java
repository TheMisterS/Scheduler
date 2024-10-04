import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserService {
    RegularUser currentUser;
    public void registerUser(Scanner scanner, Connection connection) throws SQLException, NoSuchAlgorithmException {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter email:");
        String email = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        String hashedPassword = hashPassword(password);

        String sqlInsert = "INSERT INTO scheduler.users (username, email, password_hash) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashedPassword);
            preparedStatement.executeUpdate();
            System.out.println("User registered successfully.");
        }
    }

    public boolean loginUser(Scanner scanner, Connection connection) throws SQLException, NoSuchAlgorithmException {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        String hashedPassword = hashPassword(password);

        String sqlSelect = "SELECT * FROM scheduler.users WHERE username = ? AND password_hash = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Login successful.");
                    int userId = resultSet.getInt("user_id");
                    currentUser  = new RegularUser(username, userId, scanner, connection); //creates a user with the username to check against
                    return true;
                } else {
                    System.out.println("Invalid username or password.");
                    return false;
                }
            }
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = messageDigest.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public RegularUser getCurrentUser() {
        return currentUser;
    }
}


