package data_access;

import logic.TestApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDataBase implements AutoCloseable {

    private String URL;
    private String USER;
    private String PASSWORD;
    private Connection connection = null;

    private static final Logger logger = LogManager.getLogger(TestApp.class);

    public ConnectionDataBase() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("out/resources/config.properties")) {
            properties.load(input);
            this.URL = properties.getProperty("db.url");
            this.USER = properties.getProperty("db.user");
            this.PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            logger.error("Error al leer el archivo de propiedades: " + e.getMessage(), e);
            throw e;
        }
    }

    public Connection connectDB() throws SQLException {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos: " + e.getMessage(), e);
            throw e;
        }
        return connection;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public ConnectionDataBase(String configPath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configPath)) {
            properties.load(input);
            this.URL = properties.getProperty("db.url");
            this.USER = properties.getProperty("db.user");
            this.PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            logger.error("Error al leer el archivo de propiedades: " + e.getMessage(), e);
            throw e;
        }
    }
}