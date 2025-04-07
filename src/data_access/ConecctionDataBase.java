package data_access;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConecctionDataBase {

    private String URL;
    private String USER;
    private String PASSWORD;
    private Connection connection = null;

    public ConecctionDataBase() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("out/resources/config.properties")) {
            properties.load(input);
            this.URL = properties.getProperty("db.url");
            this.USER = properties.getProperty("db.user");
            this.PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection connectDB() throws SQLException {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}