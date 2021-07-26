package de.TebosBrime.reader.configuration;

import de.TebosBrime.reader.database.DatabaseConnection;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class Config {

    private String mailServer;
    private int mailServerPort;
    private String mailUsername;
    private String mailPassword;

    private String sqlHost;
    private int sqlHostPort;
    private String sqlDatabase;
    private String sqlUsername;
    private String sqlPassword;

    private String tradeRepublicPassword;

    private String decryptedPath;
    private List<String> whitelistedSender;

    public Config() {
        try {
            File configFile = new File("configuration", "config.yml");
            if (!configFile.exists()) {
                createNewFile(configFile);
            }
            InputStream inputStream = new FileInputStream(configFile);

            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);

            Map<String, Object> mail = getNode(config, "mail");
            Map<String, Object> sql = getNode(config, "sql");
            Map<String, Object> tr = getNode(config, "tr");
            Map<String, Object> system = getNode(config, "system");

            this.mailServer = getString(mail, "host", "localhost");
            this.mailServerPort = getInt(mail, "port", 993);
            this.mailUsername = getString(mail, "username", "username");
            this.mailPassword = getString(mail, "password", "password");

            this.sqlHost = getString(sql, "host", "localhost");
            this.sqlHostPort = getInt(sql, "port", 3306);
            this.sqlDatabase = getString(sql, "database", "database");
            this.sqlUsername = getString(sql, "username", "username");
            this.sqlPassword = getString(sql, "password", "password");

            this.tradeRepublicPassword = getString(tr, "password", "password");

            this.decryptedPath = getString(system, "decryptedPath", "/home/services/decrypted/");
            this.whitelistedSender = getStringList(system, "whiteListedSender", "service@traderepublic.com");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewFile(File configFile) throws IOException {
        FileUtils.copyURLToFile(Objects.requireNonNull(getClass().getResource("/config.yml")), configFile);
    }

    private Map<String, Object> getNode(Map<String, Object> data, String key) {
        if (data.containsKey(key)) {
            return (Map<String, Object>) data.get(key);
        }
        return new HashMap<>();
    }

    private String getString(Map<String, Object> data, String key, String def) {
        return (String) data.getOrDefault(key, def);
    }

    private List<String> getStringList(Map<String, Object> data, String key, String... def) {
        return (List<String>) data.getOrDefault(key, def);
    }

    private int getInt(Map<String, Object> data, String key, int def) {
        return (Integer) data.getOrDefault(key, def);
    }

    public DatabaseConnection createDatabaseConnection() {
        return new DatabaseConnection(this.sqlHost, this.sqlHostPort, this.sqlDatabase, this.sqlUsername,
                this.sqlPassword);
    }
}
