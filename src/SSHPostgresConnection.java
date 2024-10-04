import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SSHPostgresConnection {
    private Session session;
    private Connection connection;

    public Connection connect(String sshHost, int sshPort, String sshUser, String sshPassword,
                              String dbHost, int dbPort, String dbUser, String dbPassword,
                              String dbName) throws Exception {
        JSch jsch = new JSch();
        session = jsch.getSession(sshUser, sshHost, sshPort);
        session.setPassword(sshPassword);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        int localPort = 5433;
        session.setPortForwardingL(localPort, dbHost, dbPort);

        String jdbcUrl = "jdbc:postgresql://localhost:" + localPort + "/" + dbName;
        connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}
