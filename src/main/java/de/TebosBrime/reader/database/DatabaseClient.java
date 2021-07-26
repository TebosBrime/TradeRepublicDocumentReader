package de.TebosBrime.reader.database;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseClient {

    private final DatabaseConnection databaseConnection;

    public DatabaseClient(DatabaseConnection connection) throws SQLException {
        this.databaseConnection = connection;
        this.databaseConnection.connect();
        createInvoicesTable();
    }

    public void createInvoicesTable() throws SQLException {
        this.databaseConnection.getConnection()
                .prepareStatement("CREATE TABLE IF NOT EXISTS `invoices` (\n" +
                        "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                        "  `filename` varchar(1000) NOT NULL,\n" +
                        "  `mail_subject` varchar(100) DEFAULT NULL,\n" +
                        "  `receive_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),\n" +
                        "  `data` longblob NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;").execute();
    }

    public List<String> getKnownSubjects() throws SQLException {
        List<String> knownSubjects = new ArrayList<>();

        PreparedStatement selectPreparedStatement = this.databaseConnection.getConnection()
                .prepareStatement("SELECT * FROM invoices");
        ResultSet resultSet = selectPreparedStatement.executeQuery();

        while (resultSet.next()) {
            knownSubjects.add(resultSet.getString("mail_subject"));
        }

        resultSet.close();
        selectPreparedStatement.close();
        return knownSubjects;
    }

    public void addAttachment(String fileName, String subject, Timestamp timestamp, InputStream data) throws SQLException {
        PreparedStatement preparedStatement = this.databaseConnection.getConnection()
                .prepareStatement("INSERT INTO invoices (filename, mail_subject, receive_at, data) VALUES(?,?,?,?)");
        preparedStatement.setString(1, fileName);
        preparedStatement.setString(2, subject);
        preparedStatement.setTimestamp(3, timestamp);
        preparedStatement.setBinaryStream(4, data);
        preparedStatement.executeUpdate();
    }
}
