package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.StudentProjectViewDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StudentProjectViewDAO {

    private static final Logger logger = LogManager.getLogger(StudentProjectViewDAO.class);

    private static final String SQL_SELECT_BY_PRESENTATION_ID = "SELECT idPresentacion, fecha_presentacion, tipo_presentacion, idProyecto, " + "       nombre_proyecto, matricula, nombre_estudiante " + "FROM vista_estudiantes_por_presentacion " + "WHERE idPresentacion = ?";

    public List<StudentProjectViewDTO> getStudentProjectViewByPresentationId(int presentationId) throws SQLException, IOException {
        List<StudentProjectViewDTO> studentProjectViews = new ArrayList<>();
        logger.info("Ejecutando consulta para idPresentacion: " + presentationId);

        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PRESENTATION_ID)) {

            statement.setInt(1, presentationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    StudentProjectViewDTO studentProjectView = new StudentProjectViewDTO(
                            resultSet.getInt("idPresentacion"),
                            resultSet.getDate("fecha_presentacion"),
                            resultSet.getString("tipo_presentacion"),
                            resultSet.getInt("idProyecto"),
                            resultSet.getString("nombre_proyecto"),
                            resultSet.getString("matricula"),
                            resultSet.getString("nombre_estudiante")
                    );
                    studentProjectViews.add(studentProjectView);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al ejecutar la consulta SQL.", e);
            throw e;
        }

        logger.info("Cantidad de registros obtenidos: " + studentProjectViews.size());
        return studentProjectViews;
    }

}