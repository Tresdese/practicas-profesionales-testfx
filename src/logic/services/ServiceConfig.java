package logic.services;

import data_access.ConnectionDataBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ServiceConfig {
    private static final Logger logger = LogManager.getLogger(ServiceConfig.class);

    private final ConnectionDataBase connectionDB;

    public ServiceConfig() throws SQLException, IOException {
        this.connectionDB = new ConnectionDataBase();
    }

    public UserService getUserService() throws SQLException {
        Connection connection = connectionDB.connectDataBase();
        return new UserService();
    }

    public LinkedOrganizationService getLinkedOrganizationService() throws SQLException {
        Connection connection = connectionDB.connectDataBase();
        return new LinkedOrganizationService(connection);
    }

    public RepresentativeService getRepresentativeService() throws SQLException {
        Connection connection = connectionDB.connectDataBase();
        return new RepresentativeService(connection);
    }

    public ProjectService getProjectService() throws SQLException {
        Connection connection = connectionDB.connectDataBase();
        return new ProjectService(connection);
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error cerrando la conexión: ", e);
            }
        }
    }
}