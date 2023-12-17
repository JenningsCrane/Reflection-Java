package edu.school21.DataBaseLoader;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataBaseLoader {
    private static final String DB_URL = Application.get("db.url");
    private static final String DB_LOGIN = Application.get("db.login");
    private static final String DB_PASSWORD = Application.get("db.password");
    static {
        loadDriver();
    }
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static HikariDataSource connectToDb() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(DB_URL);
        hikariConfig.setUsername(DB_LOGIN);
        hikariConfig.setPassword(DB_PASSWORD);
        return new HikariDataSource(hikariConfig);
    }
}

