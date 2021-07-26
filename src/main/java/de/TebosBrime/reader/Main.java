package de.TebosBrime.reader;

import de.TebosBrime.reader.configuration.Config;
import de.TebosBrime.reader.database.DatabaseClient;
import lombok.Getter;

import java.sql.SQLException;

public class Main {

    @Getter
    private static Main instance;
    @Getter
    private Config config;
    @Getter
    private DatabaseClient databaseClient;

    public static void main(String[] args) throws Exception {
        Main.instance = new Main();
        Main.instance.start();
    }

    public void start() throws SQLException {
        this.config = new Config();
        this.databaseClient = new DatabaseClient(this.config.createDatabaseConnection());
        new MailClient();
    }
}
