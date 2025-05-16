package logic.services;

import data_access.ConecctionDataBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class ServiceConfig {
    private static final Logger logger = LogManager.getLogger(ServiceConfig.class);

    private final ConecctionDataBase connectionDB;

    public ServiceConfig() {
        this.connectionDB = new ConecctionDataBase();
    }

    public UserService getUserService() throws SQLException {
        Connection connection = connectionDB.connectDB();
        return new UserService(connection);
    }

    public LinkedOrganizationService getLinkedOrganizationService() throws SQLException {
        Connection connection = connectionDB.connectDB();
        return new LinkedOrganizationService(connection);
    }

    public RepresentativeService getRepresentativeService() throws SQLException {
        Connection connection = connectionDB.connectDB();
        return new RepresentativeService(connection);
    }

    public ProjectService getProjectService() throws SQLException {
        Connection connection = connectionDB.connectDB();
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