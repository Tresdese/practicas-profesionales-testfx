package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.ProjectStudentViewDTO;
import logic.DTO.StudentDTO;
import logic.DTO.ProjectDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectStudentViewDAO {

    private static final Logger logger = LogManager.getLogger(ProjectStudentViewDAO.class);

    private static final String SQL_SELECT_PROJECT_BY_TUITION = "SELECT idProyecto, nombreProyecto, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion, idDepartamento FROM vista_proyecto_estudiante WHERE matricula = ?";
    private static final String SQL_SELECT_STUDENT_BY_TUITION = "SELECT matricula, nombres, apellidos, telefono, correo, usuario FROM vista_proyecto_estudiante WHERE matricula = ?";

    public ProjectDTO getProjectByTuition(String tuition) throws SQLException {
        if (tuition == null || tuition.isEmpty()) {
            return new ProjectDTO();
        }

        ProjectDTO project = new ProjectDTO();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PROJECT_BY_TUITION)) {

            statement.setString(1, tuition);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    project = new ProjectDTO(
                            resultSet.getString("idProyecto"),
                            resultSet.getString("nombreProyecto"),
                            resultSet.getString("descripcion"),
                            resultSet.getTimestamp("fechaAproximada"),
                            resultSet.getTimestamp("fechaInicio"),
                            resultSet.getString("idUsuario"),
                            resultSet.getInt("idOrganizacion"),
                            resultSet.getInt("idDepartamento")
                    );
                }
            }
        }

        return project;
    }
}