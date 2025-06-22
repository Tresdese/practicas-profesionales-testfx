package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.UserStudentViewDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserStudentViewDAO {

    private static final Logger logger = LogManager.getLogger(UserStudentViewDAO.class);

    private static final String SQL_SELECT_ALL = "SELECT * FROM vista_estudiante_usuario";
    private static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM vista_estudiante_usuario WHERE matricula = ?";

    public UserStudentViewDTO getUserStudentViewByMatricula(String matricula) throws SQLException {
        UserStudentViewDTO userStudentView = null;
        logger.info("Ejecutando consulta para obtener registro de vista_estudiante_usuario con matricula: " + matricula);

        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_MATRICULA)) {

            statement.setString(1, matricula);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userStudentView = new UserStudentViewDTO(
                        resultSet.getString("matricula"),
                        resultSet.getInt("estado"),
                        resultSet.getString("nombres_estudiante"),
                        resultSet.getString("apellidos_estudiante"),
                        resultSet.getString("telefono"),
                        resultSet.getString("correo"),
                        resultSet.getString("usuario_estudiante"),
                        resultSet.getObject("avanceCrediticio") != null ? resultSet.getInt("avanceCrediticio") : null,
                        resultSet.getBigDecimal("calificacionFinal"),
                        resultSet.getInt("NRC"),
                        resultSet.getString("nombre_grupo"),
                        resultSet.getInt("idUsuario"),
                        resultSet.getInt("numeroDePersonal"),
                        resultSet.getString("nombres_usuario"),
                        resultSet.getString("apellidos_usuario"),
                        resultSet.getString("nombreUsuario"),
                        resultSet.getString("rol")
                );
            }
        } catch (SQLException e) {
            logger.error("Error al ejecutar la consulta SQL.", e);
            throw e;
        }

        if (userStudentView != null) {
            logger.info("Registro encontrado: " + userStudentView);
        } else {
            logger.warn("No se encontró ningún registro con la matricula: " + matricula);
        }

        return userStudentView;
    }

    public List<UserStudentViewDTO> getAllUserStudentViews() throws SQLException {
        List<UserStudentViewDTO> userStudentViews = new ArrayList<>();
        logger.info("Ejecutando consulta para obtener todos los registros de vista_estudiante_usuario");

        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                UserStudentViewDTO dto = new UserStudentViewDTO(
                        resultSet.getString("matricula"),
                        resultSet.getInt("estado"),
                        resultSet.getString("nombres_estudiante"),
                        resultSet.getString("apellidos_estudiante"),
                        resultSet.getString("telefono"),
                        resultSet.getString("correo"),
                        resultSet.getString("usuario_estudiante"),
                        resultSet.getObject("avanceCrediticio") != null ? resultSet.getInt("avanceCrediticio") : null,
                        resultSet.getBigDecimal("calificacionFinal"),
                        resultSet.getInt("NRC"),
                        resultSet.getString("nombre_grupo"),
                        resultSet.getInt("idUsuario"),
                        resultSet.getInt("numeroDePersonal"),
                        resultSet.getString("nombres_usuario"),
                        resultSet.getString("apellidos_usuario"),
                        resultSet.getString("nombreUsuario"),
                        resultSet.getString("rol")
                );
                userStudentViews.add(dto);
            }
        } catch (SQLException e) {
            logger.error("Error al ejecutar la consulta SQL.", e);
            throw e;
        }

        logger.info("Cantidad de registros obtenidos: " + userStudentViews.size());
        return userStudentViews;
    }
}