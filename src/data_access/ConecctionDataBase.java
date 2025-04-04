package data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConecctionDataBase {

    private static final String URL = "jdbc:mysql://localhost:3306/revo_proyecto";
    private static final String USER = "coordinador";
    private static final String PASSWORD = "practicasprofesionales";

    private Connection connection = null;

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