package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.SelfAssessmentDTO;
import logic.interfaces.ISelfAssessmentDAO;

public class SelfAssessmentDAO implements ISelfAssessmentDAO {
    private final static String SQL_INSERT = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha_registro, estado, comentarios_generales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE autoevaluacion SET comentarios = ?, calificacion = ?, matricula = ?, idProyecto = ?, idEvidencia = ?, fecha_registro = ?, estado = ?, comentarios_generales = ? WHERE idAutoevaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM autoevaluacion WHERE idAutoevaluacion = ?";
    private final static String SQL_SELECT = "SELECT * FROM autoevaluacion WHERE idAutoevaluacion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion";

    public boolean insertSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, selfAssessment.getComments());
            statement.setBigDecimal(2, selfAssessment.getGrade());
            statement.setString(3, selfAssessment.getRegistration());
            statement.setInt(4, selfAssessment.getProjectId());
            if (selfAssessment.getEvidenceId() != null) {
                statement.setInt(5, selfAssessment.getEvidenceId());
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }
            if (selfAssessment.getRegistrationDate() != null) {
                statement.setDate(6, java.sql.Date.valueOf(selfAssessment.getRegistrationDate()));
            } else {
                statement.setNull(6, java.sql.Types.DATE);
            }
            statement.setString(7, selfAssessment.getStatus().getValue());
            statement.setString(8, selfAssessment.getGeneralComments());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, selfAssessment.getComments());
            statement.setBigDecimal(2, selfAssessment.getGrade());
            statement.setString(3, selfAssessment.getRegistration());
            statement.setInt(4, selfAssessment.getProjectId());
            if (selfAssessment.getEvidenceId() != null) {
                statement.setInt(5, selfAssessment.getEvidenceId());
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }
            if (selfAssessment.getRegistrationDate() != null) {
                statement.setDate(6, java.sql.Date.valueOf(selfAssessment.getRegistrationDate()));
            } else {
                statement.setNull(6, java.sql.Types.DATE);
            }
            statement.setString(7, selfAssessment.getStatus().getValue());
            statement.setString(8, selfAssessment.getGeneralComments());
            statement.setInt(9, selfAssessment.getSelfAssessmentId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, selfAssessment.getSelfAssessmentId());
            return statement.executeUpdate() > 0;
        }
    }

    public SelfAssessmentDTO searchSelfAssessmentById(String selfAssessmentId, Connection connection) throws SQLException {
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                0,
                "N/A",
                java.math.BigDecimal.valueOf(-1),
                "N/A",
                0,
                -1,
                java.time.LocalDate.of(1900, 1, 1),
                SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA,
                "N/A"
        );
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setInt(1, Integer.parseInt(selfAssessmentId));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    selfAssessment = mapResultSetToDTO(resultSet);
                }
            }
        }
        return selfAssessment;
    }

    public List<SelfAssessmentDTO> getAllSelfAssessments(Connection connection) throws SQLException {
        List<SelfAssessmentDTO> selfAssessments = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                selfAssessments.add(mapResultSetToDTO(resultSet));
            }
        }
        return selfAssessments;
    }

    private SelfAssessmentDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        int id = rs.getInt("idAutoevaluacion");
        String comentarios = rs.getString("comentarios");
        java.math.BigDecimal calificacion = rs.getBigDecimal("calificacion");
        String matricula = rs.getString("matricula");
        int idProyecto = rs.getInt("idProyecto");
        Integer idEvidencia = rs.getObject("idEvidencia") != null ? rs.getInt("idEvidencia") : null;
        java.sql.Date fechaRegistro = rs.getDate("fecha_registro");
        java.time.LocalDate fecha = fechaRegistro != null ? fechaRegistro.toLocalDate() : null;
        SelfAssessmentDTO.EstadoAutoevaluacion estado = SelfAssessmentDTO.EstadoAutoevaluacion.fromString(rs.getString("estado"));
        String comentariosGenerales = rs.getString("comentarios_generales");
        return new SelfAssessmentDTO(id, comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha, estado, comentariosGenerales);
    }
}
