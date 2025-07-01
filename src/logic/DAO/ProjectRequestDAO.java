package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.ProjectStatus;
import logic.interfaces.IProjectRequestDAO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectRequestDAO implements IProjectRequestDAO {
    private static final String SQL_INSERT = "INSERT INTO solicitud_proyecto (matricula, idOrganizacion, nombreProyecto, idRepresentante, descripcion, objetivoGeneral, objetivosInmediatos, objetivosMediatos, metodologia, recursos, actividades, responsabilidades, duracion, diasHorario, usuariosDirectos, usuariosIndirectos, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE solicitud_proyecto SET matricula=?, idOrganizacion=?, nombreProyecto=?, idRepresentante=?, descripcion=?, objetivoGeneral=?, objetivosInmediatos=?, objetivosMediatos=?, metodologia=?, recursos=?, actividades=?, responsabilidades=?, duracion=?, diasHorario=?, usuariosDirectos=?, usuariosIndirectos=?, estado=? WHERE idSolicitud=?";
    private static final String SQL_UPDATE_STATUS = "UPDATE solicitud_proyecto SET estado=? WHERE idSolicitud=?";
    private static final String SQL_DELETE = "DELETE FROM solicitud_proyecto WHERE idSolicitud=?";
    private static final String SQL_SELECT = "SELECT * FROM solicitud_proyecto WHERE idSolicitud=?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM solicitud_proyecto";
    private static final String SQL_SELECT_BY_TUITON = "SELECT * FROM solicitud_proyecto WHERE matricula=?";
    private static final String SQL_SELECT_BY_STATE = "SELECT * FROM solicitud_proyecto WHERE estado=?";

    @Override
    public boolean insertProjectRequest(ProjectRequestDTO request) throws SQLException, IOException {
        try (ConnectionDataBase dataBase = new ConnectionDataBase();
             Connection connection = dataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, request.getTuition());
            statement.setInt(2, Integer.parseInt(request.getOrganizationId()));
            statement.setString(3, request.getProjectName());
            statement.setInt(4, Integer.parseInt(request.getRepresentativeId()));
            statement.setString(5, request.getDescription());
            statement.setString(6, request.getGeneralObjective());
            statement.setString(7, request.getImmediateObjectives());
            statement.setString(8, request.getMediateObjectives());
            statement.setString(9, request.getMethodology());
            statement.setString(10, request.getResources());
            statement.setString(11, request.getActivities());
            statement.setString(12, request.getResponsibilities());
            statement.setInt(13, request.getDuration());
            statement.setString(14, request.getScheduleDays());
            statement.setInt(15, request.getDirectUsers());
            statement.setInt(16, request.getIndirectUsers());
            statement.setString(17, request.getStatus().getDataBaseValue());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateProjectRequest(ProjectRequestDTO request) throws SQLException, IOException {
        try (ConnectionDataBase dataBase = new ConnectionDataBase();
             Connection connection = dataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, request.getTuition());
            statement.setInt(2, Integer.parseInt(request.getOrganizationId()));
            statement.setString(3, request.getProjectName());
            statement.setInt(4, Integer.parseInt(request.getRepresentativeId()));
            statement.setString(5, request.getDescription());
            statement.setString(6, request.getGeneralObjective());
            statement.setString(7, request.getImmediateObjectives());
            statement.setString(8, request.getMediateObjectives());
            statement.setString(9, request.getMethodology());
            statement.setString(10, request.getResources());
            statement.setString(11, request.getActivities());
            statement.setString(12, request.getResponsibilities());
            statement.setInt(13, request.getDuration());
            statement.setString(14, request.getScheduleDays());
            statement.setInt(15, request.getDirectUsers());
            statement.setInt(16, request.getIndirectUsers());
            statement.setString(17, request.getStatus().getDataBaseValue());
            statement.setInt(18, request.getRequestId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateProjectRequestStatus(int requestId, ProjectStatus status) throws SQLException, IOException {
        try (ConnectionDataBase dataBase = new ConnectionDataBase();
             Connection connection = dataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATUS)) {
            statement.setString(1, status.getDataBaseValue());
            statement.setInt(2, requestId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteProjectRequest(int requestId) throws SQLException, IOException {
        try (ConnectionDataBase dataBase = new ConnectionDataBase();
             Connection connection = dataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, requestId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public ProjectRequestDTO searchProjectRequestById(int requestId) throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(-1, "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", 0, "N/A", 0, 0, null, null);
        try (ConnectionDataBase dataBase = new ConnectionDataBase();
             Connection connection = dataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setInt(1, requestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    request = mapResultSetToDTO(resultSet);
                }
            }
        }
        return request;
    }

    public List<ProjectRequestDTO> getAllProjectRequests() throws SQLException, IOException {
        List<ProjectRequestDTO> requests = new ArrayList<>();
        try (ConnectionDataBase dataBase = new ConnectionDataBase();
             Connection connection = dataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                requests.add(mapResultSetToDTO(resultSet));
            }
        }
        return requests;
    }

    public List<ProjectRequestDTO> getProjectRequestsByTuiton(String tuiton) throws SQLException, IOException {
        List<ProjectRequestDTO> requests = new ArrayList<>();
        try (ConnectionDataBase dataBase = new ConnectionDataBase();
             Connection connection = dataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_TUITON)) {
            statement.setString(1, tuiton);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapResultSetToDTO(resultSet));
                }
            }
        }
        return requests;
    }

    public ProjectRequestDTO mapResultSetToDTO(ResultSet resultSet) throws SQLException {
        return new ProjectRequestDTO(
                resultSet.getInt("idSolicitud"),
                resultSet.getString("matricula"),
                String.valueOf(resultSet.getInt("idOrganizacion")),
                String.valueOf(resultSet.getInt("idRepresentante")),
                resultSet.getString("nombreProyecto"),
                resultSet.getString("descripcion"),
                resultSet.getString("objetivoGeneral"),
                resultSet.getString("objetivosInmediatos"),
                resultSet.getString("objetivosMediatos"),
                resultSet.getString("metodologia"),
                resultSet.getString("recursos"),
                resultSet.getString("actividades"),
                resultSet.getString("responsabilidades"),
                resultSet.getInt("duracion"),
                resultSet.getString("diasHorario"),
                resultSet.getInt("usuariosDirectos"),
                resultSet.getInt("usuariosIndirectos"),
                ProjectStatus.getValueFromDataBase(resultSet.getString("estado")),
                resultSet.getString("fechaSolicitud")
        );
    }

    public List<ProjectRequestDTO> getProjectRequestsByStatus(String status) throws SQLException, IOException {
        List<ProjectRequestDTO> requests = new ArrayList<>();
        try (ConnectionDataBase dataBase = new ConnectionDataBase();
             Connection connection = dataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_STATE)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapResultSetToDTO(resultSet));
                }
            }
        }
        return requests;
    }
}