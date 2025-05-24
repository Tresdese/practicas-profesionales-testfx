package logic.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.ProjectRequestDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectRequestDAO {
    private static final String SQL_INSERT = "INSERT INTO solicitud_proyecto (matricula, idOrganizacion, idRepresentante, nombreProyecto, descripcion, objetivoGeneral, objetivosInmediatos, objetivosMediatos, metodologia, recursos, actividades, responsabilidades, duracion, diasHorario, usuariosDirectos, usuariosIndirectos, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE solicitud_proyecto SET matricula=?, idOrganizacion=?, idRepresentante=?, nombreProyecto=?, descripcion=?, objetivoGeneral=?, objetivosInmediatos=?, objetivosMediatos=?, metodologia=?, recursos=?, actividades=?, responsabilidades=?, duracion=?, diasHorario=?, usuariosDirectos=?, usuariosIndirectos=?, estado=? WHERE idSolicitud=?";
    private static final String SQL_DELETE = "DELETE FROM solicitud_proyecto WHERE idSolicitud=?";
    private static final String SQL_SELECT = "SELECT * FROM solicitud_proyecto WHERE idSolicitud=?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM solicitud_proyecto";
    private static final String SQL_SELECT_BY_TUITON = "SELECT * FROM solicitud_proyecto WHERE matricula=?";

    public boolean insertProjectRequest(ProjectRequestDTO request) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, request.getTuiton());
            stmt.setInt(2, request.getOrganizationId());
            stmt.setInt(3, request.getRepresentativeId());
            stmt.setString(4, request.getProjectName());
            stmt.setString(5, request.getDescription());
            stmt.setString(6, request.getGeneralObjective());
            stmt.setString(7, request.getImmediateObjectives());
            stmt.setString(8, request.getMediateObjectives());
            stmt.setString(9, request.getMethodology());
            stmt.setString(10, request.getResources());
            stmt.setString(11, request.getActivities());
            stmt.setString(12, request.getResponsibilities());
            stmt.setInt(13, request.getDuration());
            stmt.setString(14, request.getScheduleDays());
            stmt.setInt(15, request.getDirectUsers());
            stmt.setInt(16, request.getIndirectUsers());
            stmt.setString(17, request.getStatus().name());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateProjectRequest(ProjectRequestDTO request) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, request.getTuiton());
            stmt.setInt(2, request.getOrganizationId());
            stmt.setInt(3, request.getRepresentativeId());
            stmt.setString(4, request.getProjectName());
            stmt.setString(5, request.getDescription());
            stmt.setString(6, request.getGeneralObjective());
            stmt.setString(7, request.getImmediateObjectives());
            stmt.setString(8, request.getMediateObjectives());
            stmt.setString(9, request.getMethodology());
            stmt.setString(10, request.getResources());
            stmt.setString(11, request.getActivities());
            stmt.setString(12, request.getResponsibilities());
            stmt.setInt(13, request.getDuration());
            stmt.setString(14, request.getScheduleDays());
            stmt.setInt(15, request.getDirectUsers());
            stmt.setInt(16, request.getIndirectUsers());
            stmt.setString(17, request.getStatus().name());
            stmt.setInt(18, request.getRequestId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteProjectRequest(int requestId) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ProjectRequestDTO searchProjectRequestById(int requestId) throws SQLException {
        ProjectRequestDTO request = null;
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    request = mapResultSet(rs);
                }
            }
        }
        return request;
    }

    public List<ProjectRequestDTO> getAllProjectRequests() throws SQLException {
        List<ProjectRequestDTO> list = new ArrayList<>();
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<ProjectRequestDTO> searchProjectRequestsByTuiton(String tuiton) throws SQLException {
        List<ProjectRequestDTO> list = new ArrayList<>();
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_TUITON)) {
            stmt.setString(1, tuiton);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    private ProjectRequestDTO mapResultSet(ResultSet rs) throws SQLException {
        return new ProjectRequestDTO(
                rs.getInt("idSolicitud"),
                rs.getString("matricula"),
                rs.getInt("idOrganizacion"),
                rs.getInt("idRepresentante"),
                rs.getString("nombreProyecto"),
                rs.getString("descripcion"),
                rs.getString("objetivoGeneral"),
                rs.getString("objetivosInmediatos"),
                rs.getString("objetivosMediatos"),
                rs.getString("metodologia"),
                rs.getString("recursos"),
                rs.getString("actividades"),
                rs.getString("responsabilidades"),
                rs.getInt("duracion"),
                rs.getString("diasHorario"),
                rs.getInt("usuariosDirectos"),
                rs.getInt("usuariosIndirectos"),
                rs.getString("estado"), // se mantiene como String
                rs.getString("fechaSolicitud")
        );
    }
}
