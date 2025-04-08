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
    private final static String SQL_INSERT = "INSERT INTO autoevaluacion (idAutoevaluacion, comentarios, calificacion, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE autoevaluacion SET comentarios = ?, calificacion = ?, matricula = ?, idEvidencia = ? WHERE idAutoevaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM autoevaluacion WHERE idAutoevaluacion = ?";
    private final static String SQL_SELECT = "SELECT * FROM autoevaluacion WHERE idAutoevaluacion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion";

    public boolean insertSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, selfAssessment.getSelfAssessmentId());
            ps.setString(2, selfAssessment.getComments());
            ps.setDouble(3, selfAssessment.getGrade());
            ps.setString(4, selfAssessment.getRegistration());
            ps.setString(5, selfAssessment.getEvidenceId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, selfAssessment.getComments());
            ps.setDouble(2, selfAssessment.getGrade());
            ps.setString(3, selfAssessment.getRegistration());
            ps.setString(4, selfAssessment.getEvidenceId());
            ps.setString(5, selfAssessment.getSelfAssessmentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, selfAssessment.getSelfAssessmentId());
            return ps.executeUpdate() > 0;
        }
    }

    public SelfAssessmentDTO getSelfAssessment(String selfAssessmentId, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, selfAssessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SelfAssessmentDTO(
                        rs.getString("idAutoevaluacion"),
                        rs.getString("comentarios"),
                        rs.getDouble("calificacion"),
                        rs.getString("matricula"),
                        rs.getString("idEvidencia")
                    );
                }
            }
        }
        return null;
    }

    public List<SelfAssessmentDTO> getAllSelfAssessments(Connection connection) throws SQLException {
        List<SelfAssessmentDTO> selfAssessments = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                selfAssessments.add(new SelfAssessmentDTO(
                    rs.getString("idAutoevaluacion"),
                    rs.getString("comentarios"),
                    rs.getDouble("calificacion"),
                    rs.getString("matricula"),
                    rs.getString("idEvidencia")
                ));
            }
        }
        return selfAssessments;
    }
}
