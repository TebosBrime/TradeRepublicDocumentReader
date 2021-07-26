package de.TebosBrime.reader.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final String user;
    private final String database;
    private final String password;
    private final int port;
    private final String hostname;
    private Connection connection = null;

    public DatabaseConnection(String hostname, int port, String database, String user, String password) {
        this.user = user;
        this.database = database;
        this.password = password;
        this.port = port;
        this.hostname = hostname;
    }

    public Connection getConnection() {
        if (!this.isConnected()) {
            System.err.println("[DB] Lost DB Connection. Reconnecting...");
            this.connect();
        }
        return this.connection;
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public void connect() {
        if (this.isConnected()) {
            disconnect();
        }

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String url = "jdbc:mariadb://" + this.hostname + ":" + this.port + "/" + this.database + "?passwordCharacterEncoding";
            this.connection = DriverManager.getConnection(url, this.user, this.password);
            System.out.println("[DB] Connected to database!");
        } catch (SQLException | ClassNotFoundException var2) {
            System.err.println("[DB] Could not connect to MySQL server! Because: " + var2.getMessage());
        }

    }

    public void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
                System.out.println("[DB] Closed connection to database!");
            } catch (SQLException var2) {
                System.err.println("[DB] Error while closing the MySQL Connection!");
                var2.printStackTrace();
            }
        }
    }
}
