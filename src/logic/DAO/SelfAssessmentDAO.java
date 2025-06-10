package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.SelfAssessmentDTO;
import logic.interfaces.ISelfAssessmentDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelfAssessmentDAO implements ISelfAssessmentDAO {
    private final static String SQL_INSERT = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha_registro, estado, comentarios_generales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE autoevaluacion SET comentarios = ?, calificacion = ?, matricula = ?, idProyecto = ?, idEvidencia = ?, fecha_registro = ?, estado = ?, comentarios_generales = ? WHERE idAutoevaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM autoevaluacion WHERE idAutoevaluacion = ?";
    private final static String SQL_SELECT = "SELECT * FROM autoevaluacion WHERE idAutoevaluacion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion";

    @Override
    public boolean insertSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException {
        try (ConnectionDataBase db = new ConnectionDataBase();
             Connection connection = db.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, selfAssessment.getComments());
            statement.setFloat(2, selfAssessment.getGrade());
            statement.setString(3, selfAssessment.getRegistration());
            statement.setInt(4, selfAssessment.getProjectId());
            statement.setInt(5, selfAssessment.getEvidenceId());
            if (selfAssessment.getRegistrationDate() != null) {
                statement.setDate(6, new java.sql.Date(selfAssessment.getRegistrationDate().getTime()));
            } else {
                statement.setNull(6, Types.DATE);
            }
            statement.setString(7, selfAssessment.getStatus().getValue());
            statement.setString(8, selfAssessment.getGeneralComments());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException {
        try (ConnectionDataBase db = new ConnectionDataBase();
             Connection connection = db.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, selfAssessment.getComments());
            statement.setFloat(2, selfAssessment.getGrade());
            statement.setString(3, selfAssessment.getRegistration());
            statement.setInt(4, selfAssessment.getProjectId());
            statement.setInt(5, selfAssessment.getEvidenceId());
            if (selfAssessment.getRegistrationDate() != null) {
                statement.setDate(6, new java.sql.Date(selfAssessment.getRegistrationDate().getTime()));
            } else {
                statement.setNull(6, Types.DATE);
            }
            statement.setString(7, selfAssessment.getStatus().getValue());
            statement.setString(8, selfAssessment.getGeneralComments());
            statement.setInt(9, selfAssessment.getSelfAssessmentId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException {
        try (ConnectionDataBase db = new ConnectionDataBase();
             Connection connection = db.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, selfAssessment.getSelfAssessmentId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public SelfAssessmentDTO searchSelfAssessmentById(String selfAssessmentId) throws SQLException {
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                0,
                "N/A",
                -1f,
                "N/A",
                0,
                0,
                null,
                SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA,
                "N/A"
        );
        try (ConnectionDataBase db = new ConnectionDataBase();
             Connection connection = db.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setInt(1, Integer.parseInt(selfAssessmentId));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    selfAssessment = mapResultSetToDTO(resultSet);
                }
            }
        }
        return selfAssessment;
    }

    @Override
    public List<SelfAssessmentDTO> getAllSelfAssessments() throws SQLException {
        List<SelfAssessmentDTO> selfAssessments = new ArrayList<>();
        try (ConnectionDataBase db = new ConnectionDataBase();
             Connection connection = db.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                selfAssessments.add(mapResultSetToDTO(resultSet));
            }
        }
        return selfAssessments;
    }

    public int getLastSelfAssessmentId() throws Exception {
        int lastId = -1;
        try (ConnectionDataBase db = new ConnectionDataBase();
             Connection connection = db.connectDB();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(idAutoevaluacion) FROM autoevaluacion")) {
            if (rs.next()) {
                lastId = rs.getInt(1);
            }
        }
        if (lastId == -1) {
            throw new Exception("No se pudo obtener el id de la autoevaluación.");
        }
        return lastId;
    }

    private SelfAssessmentDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        int id = rs.getInt("idAutoevaluacion");
        String comments = rs.getString("comentarios");
        float grade = rs.getFloat("calificacion");
        String registration = rs.getString("matricula");
        int projectId = rs.getInt("idProyecto");
        int evidenceId = rs.getInt("idEvidencia");
        java.sql.Date regDate = rs.getDate("fecha_registro");
        String estado = rs.getString("estado");
        String generalComments = rs.getString("comentarios_generales");
        return new SelfAssessmentDTO(
                id,
                comments,
                grade,
                registration,
                projectId,
                evidenceId,
                regDate != null ? new java.util.Date(regDate.getTime()) : null,
                SelfAssessmentDTO.EstadoAutoevaluacion.fromString(estado),
                generalComments
        );
    }
}