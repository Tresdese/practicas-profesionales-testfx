package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.GroupDTO;
import logic.interfaces.IGroupDAO;

public class GroupDAO implements IGroupDAO {
    private final static String SQL_INSERT = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE grupo SET nombre = ?, idUsuario = ?, idPeriodo = ? WHERE NRC = ?";
    private final static String SQL_DELETE = "DELETE FROM grupo WHERE NRC = ?";
    private final static String SQL_SELECT = "SELECT * FROM grupo WHERE NRC = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM grupo";

    public boolean insertGroup(GroupDTO group, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, group.getNRC());
            ps.setString(2, group.getName());
            ps.setString(3, group.getIdUser());
            ps.setString(4, group.getIdPeriod());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateGroup(GroupDTO group, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, group.getName());
            ps.setString(2, group.getIdUser());
            ps.setString(3, group.getIdPeriod());
            ps.setString(4, group.getNRC());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteGroup(String NRC, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, NRC);
            return ps.executeUpdate() > 0;
        }
    }

    public GroupDTO getGroup(String NRC, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, NRC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GroupDTO(rs.getString("NRC"), rs.getString("nombre"), rs.getString("idUsuario"), rs.getString("idPeriodo"));
                }
            }
        }
        return null;
    }

    public List<GroupDTO> getAllGroups(Connection connection) throws SQLException {
        List<GroupDTO> groups = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                groups.add(new GroupDTO(rs.getString("NRC"), rs.getString("nombre"), rs.getString("idUsuario"), rs.getString("idPeriodo")));
            }
        }
        return groups;
    }
}
