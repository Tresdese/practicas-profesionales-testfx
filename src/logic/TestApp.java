package logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import data_access.ConnectionDataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class TestApp {
    private static final Logger logger = LogManager.getLogger(TestApp.class);

    public static void main(String[] args) {
        logger.info("Iniciando la aplicación de prueba...");

        ConnectionDataBase db = new ConnectionDataBase();
        try (Connection connection = db.connectDB()) {
            if (connection != null) {
                logger.info("Conexión a la base de datos establecida correctamente.");
            }
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos: ", e);
        }

        logger.info("Finalizando la aplicación de prueba.");
    }
}