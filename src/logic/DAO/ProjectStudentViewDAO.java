package logic.DAO;

import data_access.ConecctionDataBase;
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

    private static final String SQL_SELECT_PROJECT_BY_TUITION = "SELECT idProyecto, nombreProyecto, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion FROM vista_proyecto_estudiante WHERE matricula = ?";
    private static final String SQL_SELECT_STUDENT_BY_TUITION = "SELECT matricula, nombres, apellidos, telefono, correo, usuario,  FROM vista_proyecto_estudiante WHERE matricula = ?";

    public ProjectDTO getProjectByTuition(String tuition) throws SQLException {
        logger.info("Obteniendo proyecto para matrícula: " + tuition);
        ProjectDTO project = null;
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PROJECT_BY_TUITION)) {

            statement.setString(1, tuition);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    project = new ProjectDTO(
                            resultSet.getString("projectId"),
                            resultSet.getString("projectName"),
                            resultSet.getString("description"),
                            resultSet.getTimestamp("approximateDate"),
                            resultSet.getTimestamp("startDate"),
                            resultSet.getString("idUsuario"),
                            resultSet.getInt("idOrganizacion")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener el proyecto para la matrícula: " + tuition, e);
            throw e;
        }
        return project;
    }

//    public StudentDTO getStudentByTuition(String tuition) throws SQLException {
//        logger.info("Obteniendo nombre del estudiante para matrícula: " + tuition);
//        String studentName = null;
//        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
//             Connection connection = connectionDataBase.connectDB();
//             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_STUDENT_BY_TUITION)) {
//
//            statement.setString(1, tuition);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    studentName = resultSet.getString("studentName");
//                }
//            }
//        } catch (SQLException e) {
//            logger.error("Error al obtener el nombre del estudiante para la matrícula: " + tuition, e);
//            throw e;
//        }
//        return studentName;
//    }
}